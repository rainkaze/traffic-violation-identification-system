// rainkaze/traffic-violation-identification-system/traffic-violation-identification-system-feature-login/frontend/src/views/auth/RegisterView.vue
<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100">
    <div class="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
      <div v-if="!isSubmitted">
        <div class="text-center">
          <h1 class="text-3xl font-bold text-gray-900">执法员注册申请</h1>
          <p class="mt-2 text-gray-600">请填写您的信息，提交后需等待管理员审核</p>
        </div>
        <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
          <div>
            <label for="fullName" class="text-sm font-medium text-gray-700">姓名</label>
            <input v-model="form.fullName" type="text" required class="input mt-1 w-full">
          </div>
          <div>
            <label for="username" class="text-sm font-medium text-gray-700">警号 / 用户名</label>
            <input v-model="form.username" type="text" required class="input mt-1 w-full">
          </div>
          <div>
            <label for="email" class="text-sm font-medium text-gray-700">邮箱</label>
            <div class="flex items-center space-x-2 mt-1">
              <input v-model="form.email" type="email" required class="input w-full">
              <button @click="sendCode" type="button" :disabled="isCodeSending || countdown > 0" class="btn btn-secondary whitespace-nowrap disabled:opacity-50">
                <span v-if="isCodeSending">发送中...</span>
                <span v-else-if="countdown > 0">{{ countdown }}s后重发</span>
                <span v-else>发送验证码</span>
              </button>
            </div>
          </div>
          <div>
            <label for="verificationCode" class="text-sm font-medium text-gray-700">验证码</label>
            <input v-model="form.verificationCode" type="text" required class="input mt-1 w-full" placeholder="请输入6位邮箱验证码">
          </div>
          <div>
            <label for="rank" class="text-sm font-medium text-gray-700">申请等级</label>
            <select v-model="form.rank" class="input mt-1 w-full">
              <option>警员</option>
              <option>中队长</option>
              <option>大队长</option>
            </select>
          </div>
          <div v-if="error" class="text-sm text-red-600">{{ error }}</div>
          <div v-if="info" class="text-sm text-green-600">{{ info }}</div>
          <div>
            <button type="submit" :disabled="isLoading" class="w-full btn btn-primary disabled:opacity-50">
              <span v-if="isLoading">提交中...</span>
              <span v-else>提交申请</span>
            </button>
          </div>
        </form>
        <div class="text-center mt-6">
          <p class="text-sm text-gray-600">
            已有账户?
            <router-link to="/login" class="font-medium text-primary hover:underline">
              返回登录
            </router-link>
          </p>
        </div>
      </div>
      <div v-else class="text-center space-y-4">
        <i class="fa fa-check-circle text-5xl text-success"></i>
        <h2 class="text-2xl font-bold">申请已提交</h2>
        <p class="text-gray-600">您的注册申请已成功提交，请等待管理员审核。批准后，我们会将初始密码发送到您的注册邮箱。</p>
        <router-link to="/login" class="inline-block btn btn-primary">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import apiClient from '@/services/api';

const form = reactive({
  fullName: '',
  username: '',
  email: '',
  rank: '警员',
  verificationCode: ''
});
const error = ref('');
const info = ref('');
const isLoading = ref(false);
const isSubmitted = ref(false);
const isCodeSending = ref(false);
const countdown = ref(0);

const sendCode = async () => {
  if (!form.email) {
    error.value = '请输入邮箱地址。';
    return;
  }
  isCodeSending.value = true;
  error.value = '';
  info.value = '';

  try {
    const response = await apiClient.post('/auth/send-verification-code', { email: form.email });
    info.value = response.data.message;

    // 开始倒计时
    countdown.value = 60;
    const interval = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(interval);
      }
    }, 1000);

  } catch (err) {
    error.value = err.response?.data?.message || '发送验证码失败。';
  } finally {
    isCodeSending.value = false;
  }
};

const handleRegister = async () => {
  isLoading.value = true;
  error.value = '';
  info.value = '';
  try {
    await apiClient.post('/auth/register', form);
    isSubmitted.value = true;
  } catch (err) {
    error.value = err.response?.data?.message || '注册申请失败，请检查您填写的信息。';
  } finally {
    isLoading.value = false;
  }
};
</script>
