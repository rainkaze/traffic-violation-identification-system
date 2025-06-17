<template>
  <div v-if="isAuthenticated" class="flex h-screen overflow-hidden bg-gray-50 font-sans">
    <Sidebar :is-mobile-open="isSidebarOpen" :toggle-sidebar="toggleSidebar" />
    <div class="flex-1 flex flex-col overflow-hidden">
      <Header :toggle-sidebar="toggleSidebar" />
      <main class="flex-1 overflow-y-auto bg-gray-50 p-4">
        <router-view />
      </main>
    </div>
  </div>
  <div v-else>
    <router-view />
  </div>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Sidebar from './components/Sidebar.vue';
import Header from './components/Header.vue';
import authStore from './store/auth';
import apiClient from './services/api';
import { jwtDecode } from "jwt-decode";


const isSidebarOpen = ref(false);
const route = useRoute();
const router = useRouter();

// 计算属性，判断用户是否已登录
const isAuthenticated = computed(() => authStore.isAuthenticated());

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

// 检查 token 并获取用户信息
const checkAuth = async () => {
  const token = authStore.state.token;
  if (token && !authStore.currentUser()) {
    try {
      const decoded = jwtDecode(token);
      const response = await apiClient.get(`/users/${decoded.sub}`);
      authStore.setUser(response.data);
    } catch (error) {
      console.error("Session restore failed", error);
      authStore.logout();
      router.push('/login');
    }
  }
};

// 组件挂载时检查认证状态
onMounted(checkAuth);


// 当路由变化时，如果移动端侧边栏是打开的，就关闭它
watch(route, () => {
  if (isSidebarOpen.value) {
    isSidebarOpen.value = false;
  }
});
</script>

<style>
/* 移除所有 scoped 和 App.vue 特有的样式，因为全局样式现在由 main.css 控制 */
</style>
