<template>
  <div class="p-4 sm:p-6 lg:p-8">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">个人信息管理</h2>
      <p class="text-gray-600">在这里更新您的个人资料和修改密码</p>
    </div>

    <div class="max-w-4xl mx-auto space-y-8">
      <div class="card">
        <h3 class="font-bold text-lg text-gray-800 mb-6">个人资料</h3>
        <form @submit.prevent="handleProfileUpdate">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="md:col-span-1 flex flex-col items-center">
              <img :src="profileForm.avatarUrl || 'https://picsum.photos/id/1005/128/128'" alt="User Avatar" class="w-32 h-32 rounded-full mb-4 object-cover ring-4 ring-primary/20">
              <button type="button" class="btn btn-secondary text-sm">上传新头像</button>
              <p class="text-xs text-gray-500 mt-2">（功能待实现）</p>
            </div>
            <div class="md:col-span-2 space-y-4">
              <div><label class="block text-sm font-medium text-gray-700">姓名</label><input type="text" v-model="profileForm.fullName" class="mt-1 block w-full input" required></div>
              <div><label class="block text-sm font-medium text-gray-700">邮箱地址</label><input type="email" v-model="profileForm.email" class="mt-1 block w-full input" required></div>
              <div><label class="block text-sm font-medium text-gray-700">警号 / 用户名</label><input type="text" :value="profileForm.username" class="mt-1 block w-full input bg-gray-100" disabled></div>
              <div><label class="block text-sm font-medium text-gray-700">等级</label><input type="text" :value="profileForm.rank" class="mt-1 block w-full input bg-gray-100" disabled></div>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button type="submit" class="btn btn-primary" :disabled="isSavingProfile">
              {{ isSavingProfile ? '保存中...' : '保存个人信息' }}
            </button>
          </div>
        </form>
      </div>

      <div class="card">
        <h3 class="font-bold text-lg text-gray-800 mb-6">修改密码</h3>
        <form @submit.prevent="handlePasswordChange" class="max-w-md">
          <div class="space-y-4">
            <div><label class="block text-sm font-medium text-gray-700">当前密码</label><input type="password" v-model="passwordForm.currentPassword" class="mt-1 block w-full input" required></div>
            <div><label class="block text-sm font-medium text-gray-700">新密码</label><input type="password" v-model="passwordForm.newPassword" class="mt-1 block w-full input" required placeholder="至少8位字符"></div>
            <div><label class="block text-sm font-medium text-gray-700">确认新密码</label><input type="password" v-model="passwordForm.confirmPassword" class="mt-1 block w-full input" required></div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button type="submit" class="btn btn-primary" :disabled="isSavingPassword">
              {{ isSavingPassword ? '修改中...' : '确认修改密码' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import authStore from '@/store/auth';
import apiClient from '@/services/api';

const router = useRouter();

const profileForm = reactive({
  fullName: '',
  email: '',
  username: '',
  rank: '',
  avatarUrl: '',
});

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const isSavingProfile = ref(false);
const isSavingPassword = ref(false);

const loadUserProfile = () => {
  const currentUser = authStore.currentUser();
  if (currentUser) {
    Object.assign(profileForm, currentUser);
  }
};

const handleProfileUpdate = async () => {
  isSavingProfile.value = true;
  try {
    const payload = {
      fullName: profileForm.fullName,
      email: profileForm.email,
    };
    const response = await apiClient.put('/users/profile', payload);
    authStore.setUser(response.data); // 更新 store 中的用户信息
    alert('个人信息更新成功！');
  } catch (error) {
    alert(error.response?.data?.message || '更新失败');
  } finally {
    isSavingProfile.value = false;
  }
};

const handlePasswordChange = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    alert('新密码和确认密码不匹配！');
    return;
  }
  if (passwordForm.newPassword.length < 8) {
    alert('新密码长度至少为8位！');
    return;
  }
  isSavingPassword.value = true;
  try {
    const payload = {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    };
    const response = await apiClient.post('/users/profile/change-password', payload);
    alert(response.data.message || '密码修改成功！您将被登出，请使用新密码重新登录。');

    // 清空表单并退出登录
    Object.assign(passwordForm, { currentPassword: '', newPassword: '', confirmPassword: '' });
    authStore.logout();

    // 跳转到登录页
    router.push('/login');

  } catch (error) {
    alert(error.response?.data?.message || '密码修改失败');
  } finally {
    isSavingPassword.value = false;
  }
};

onMounted(loadUserProfile);
</script>
