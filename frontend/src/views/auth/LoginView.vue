<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100">
    <div class="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
      <div class="text-center">
        <h1 class="text-3xl font-bold text-gray-900">交通违法管理系统</h1>
        <p class="mt-2 text-gray-600">欢迎回来，请登录您的账户</p>
      </div>
      <form class="space-y-6" @submit.prevent="handleLogin">
        <div>
          <label for="username" class="text-sm font-medium text-gray-700">警号 / 用户名</label>
          <input v-model="username" id="username" name="username" type="text" required
                 class="input mt-1 w-full" placeholder="请输入您的用户名">
        </div>
        <div>
          <label for="password" class="text-sm font-medium text-gray-700">密码</label>
          <input v-model="password" id="password" name="password" type="password" required
                 class="input mt-1 w-full" placeholder="请输入您的密码">
        </div>
        <div v-if="error" class="text-sm text-red-600">
          {{ error }}
        </div>
        <div>
          <button type="submit" :disabled="isLoading"
                  class="w-full btn btn-primary disabled:opacity-50">
            <span v-if="isLoading">登录中...</span>
            <span v-else>登录</span>
          </button>
        </div>
      </form>
      <div class="text-center">
        <p class="text-sm text-gray-600">
          还没有账户?
          <router-link to="/register" class="font-medium text-primary hover:underline">
            申请注册
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/services/api';
import authStore from '@/store/auth';

const router = useRouter();

const username = ref('admin'); // 预填管理员方便测试
const password = ref('password123');
const error = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    const response = await apiClient.post('/auth/login', { username: username.value, password: password.value });
    const { token, user } = response.data;

    authStore.setToken(token);
    authStore.setUser(user);

    // 登录成功后跳转到仪表盘
    await router.push({ name: 'dashboard' });

  } catch (err) {
    if (err.response && err.response.data) {
      error.value = err.response.data.message || '登录失败，请检查您的用户名和密码。';
    } else {
      error.value = '无法连接到服务器，请稍后再试。';
    }
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};
</script>
