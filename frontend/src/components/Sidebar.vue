<template>
  <aside class="bg-dark w-64 hidden md:block transition-all duration-300 ease-in-out z-20 flex-shrink-0">
    <div class="p-4 border-b border-gray-700">
      <h1 class="text-white text-xl font-bold flex items-center gap-2">
        <i class="fa fa-road"></i>
<!--        <span>交通违法管理系统</span>-->
        {{ systemName }}
      </h1>
    </div>
    <nav class="p-4">
      <ul class="space-y-1">
        <li>
          <router-link to="/dashboard" class="sidebar-item">
            <i class="fa fa-dashboard w-5 text-center"></i>
            <span>仪表盘</span>
          </router-link>
        </li>
        <li v-if="isAdmin">
          <router-link to="/user-management" class="sidebar-item">
            <i class="fa fa-users w-5 text-center"></i>
            <span>用户管理</span>
          </router-link>
        </li>
        <li v-if="isAdmin">
          <router-link to="/workflow-management" class="sidebar-item">
            <i class="fa fa-sitemap w-5 text-center"></i>
            <span>工作流管理</span>
          </router-link>
        </li>
        <li>
          <router-link to="/violations" class="sidebar-item">
            <i class="fa fa-list-alt w-5 text-center"></i>
            <span>违法记录</span>
          </router-link>
        </li>
        <li>
          <router-link to="/monitoring" class="sidebar-item">
            <i class="fa fa-video-camera w-5 text-center"></i>
            <span>实时监控</span>
          </router-link>
        </li>
        <li>
          <router-link to="/enforcement" class="sidebar-item">
            <i class="fa fa-gavel w-5 text-center"></i>
            <span>执法管理</span>
          </router-link>
        </li>
        <li>
          <router-link to="/accidents" class="sidebar-item">
            <i class="fa fa-car w-5 text-center"></i>
            <span>事故处置</span>
          </router-link>
        </li>
        <li>
          <router-link to="/statistics" class="sidebar-item">
            <i class="fa fa-bar-chart w-5 text-center"></i>
            <span>统计分析</span>
          </router-link>
        </li>
        <li>
          <router-link to="/devices" class="sidebar-item">
            <i class="fa fa-camera w-5 text-center"></i>
            <span>设备管理</span>
          </router-link>
        </li>
        <li>
          <router-link to="/settings" class="sidebar-item">
            <i class="fa fa-cog w-5 text-center"></i>
            <span>系统设置</span>
          </router-link>
        </li>
      </ul>
      <div class="mt-8 pt-4 border-t border-gray-700">
        <ul class="space-y-1">
          <li>
            <router-link to="/help" class="sidebar-item">
              <i class="fa fa-question-circle w-5 text-center"></i>
              <span>帮助中心</span>
            </router-link>
          </li>
          <li>
            <a href="#" class="sidebar-item" @click.prevent="handleLogout">
              <i class="fa fa-sign-out w-5 text-center"></i>
              <span>退出登录</span>
            </a>
          </li>
        </ul>
      </div>
    </nav>
  </aside>

  <div
    v-if="isMobileOpen"
    @click="toggleSidebar"
    class="fixed inset-0 bg-black bg-opacity-50 z-10 md:hidden"
  ></div>

  <aside
    :class="['bg-dark w-64 transition-all duration-300 ease-in-out z-20 fixed md:hidden', isMobileOpen ? 'translate-x-0' : '-translate-x-full']"
    style="height: 100vh;"
  >
    <div class="p-4 border-b border-gray-700">
      <h1 class="text-white text-xl font-bold flex items-center gap-2">
        <i class="fa fa-road"></i>
        <span>交通违法管理系统</span>
      </h1>
    </div>
    <nav class="p-4">
      <ul class="space-y-1">
        <li>
          <router-link to="/dashboard" class="sidebar-item">
            <i class="fa fa-dashboard w-5 text-center"></i>
            <span>仪表盘</span>
          </router-link>
        </li>
        <li v-if="isAdmin">
          <router-link to="/user-management" class="sidebar-item">
            <i class="fa fa-users w-5 text-center"></i>
            <span>用户管理</span>
          </router-link>
        </li>
        <li v-if="isAdmin">
          <router-link to="/workflow-management" class="sidebar-item">
            <i class="fa fa-sitemap w-5 text-center"></i>
            <span>工作流管理</span>
          </router-link>
        </li>
        <li>
          <router-link to="/violations" class="sidebar-item">
            <i class="fa fa-list-alt w-5 text-center"></i>
            <span>违法记录</span>
          </router-link>
        </li>
      </ul>
      <div class="mt-8 pt-4 border-t border-gray-700">
        <ul class="space-y-1">
          <li>
            <router-link to="/help" class="sidebar-item">
              <i class="fa fa-question-circle w-5 text-center"></i>
              <span>帮助中心</span>
            </router-link>
          </li>
          <li>
            <a href="#" class="sidebar-item" @click.prevent="handleLogout">
              <i class="fa fa-sign-out w-5 text-center"></i>
              <span>退出登录</span>
            </a>
          </li>
        </ul>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import {computed} from 'vue';
import {useRouter} from 'vue-router';
import authStore from '@/store/auth';


import { useSystemConfigStore } from '@/store/systemConfig'

const configStore = useSystemConfigStore()

// 计算属性绑定系统名称，自动响应变化
const systemName = computed(() => configStore.systemName)

defineProps({
  isMobileOpen: Boolean,
  toggleSidebar: Function,
});

const router = useRouter();
const isAdmin = computed(() => authStore.isAdmin());

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    authStore.logout();
    router.push('/login');
  }
};
</script>
