<template>
  <div class="flex h-screen overflow-hidden bg-gray-50 font-sans">
    <Sidebar :is-mobile-open="isSidebarOpen" :toggle-sidebar="toggleSidebar" />
    <div class="flex-1 flex flex-col overflow-hidden">
      <Header :toggle-sidebar="toggleSidebar" />
      <main class="flex-1 overflow-y-auto bg-gray-50 p-4">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Sidebar from './components/Sidebar.vue';
import Header from './components/Header.vue';

const isSidebarOpen = ref(false);
const route = useRoute();

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

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
