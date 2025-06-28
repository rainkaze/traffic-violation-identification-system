<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 z-40 flex justify-center items-center" @click.self="close">
    <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md mx-4">
      <h3 class="text-xl font-bold mb-4">{{ isEditMode ? '编辑用户' : '新建用户' }}</h3>
      <form @submit.prevent="submitForm">
        <div class="space-y-4">
          <div>
            <label for="fullName" class="block text-sm font-medium text-gray-700">姓名</label>
            <input v-model="form.fullName" type="text" id="fullName" class="input mt-1 w-full" required>
          </div>
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700">用户名/警号</label>
            <input v-model="form.username" type="text" id="username" class="input mt-1 w-full" :disabled="isEditMode" required>
            <p v-if="isEditMode" class="text-xs text-gray-500 mt-1">用户名不可修改。</p>
          </div>
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700">邮箱</label>
            <input v-model="form.email" type="email" id="email" class="input mt-1 w-full" required>
          </div>
          <div>
            <label for="rank" class="block text-sm font-medium text-gray-700">等级</label>
            <select v-model="form.rank" id="rank" class="input mt-1 w-full" required>
              <option>警员</option>
              <option>中队长</option>
              <option>大队长</option>
              <option>管理员</option>
            </select>
          </div>
        </div>
        <div v-if="error" class="text-red-600 text-sm mt-4">{{ error }}</div>
        <div class="mt-6 flex justify-end gap-3">
          <button type="button" @click="close" class="btn btn-secondary">取消</button>
          <button type="submit" :disabled="isLoading" class="btn btn-primary">
            {{ isLoading ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import apiClient from '@/services/api.js';

const props = defineProps({
  show: Boolean,
  user: Object, // The user object for editing, null for creating
});

const emit = defineEmits(['close', 'save']);

const form = ref({});
const error = ref('');
const isLoading = ref(false);

const isEditMode = computed(() => !!props.user);

// Watch for the 'user' prop to change and reset the form
watch(() => props.user, (newUser) => {
  if (newUser) {
    // Edit mode: copy user data to form
    form.value = { ...newUser };
  } else {
    // Create mode: reset form
    form.value = {
      fullName: '',
      username: '',
      email: '',
      rank: '警员',
    };
  }
  error.value = ''; // Clear previous errors
}, { immediate: true });

const close = () => {
  emit('close');
};

const submitForm = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    const payload = {
      fullName: form.value.fullName,
      username: form.value.username,
      email: form.value.email,
      rank: form.value.rank,
    };

    if (isEditMode.value) {
      // Update user
      await apiClient.put(`/admin/users/${props.user.userId}`, payload);
    } else {
      // Create user
      await apiClient.post('/admin/users', payload);
    }
    emit('save'); // Notify parent component to refresh the list
    close();
  } catch (err) {
    error.value = err.response?.data?.message || `操作失败：${err.message}`;
  } finally {
    isLoading.value = false;
  }
};
</script>
