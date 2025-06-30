<template>
  <header class="bg-white shadow-sm z-10">
    <div class="flex items-center justify-between p-4">
      <div class="flex items-center">
        <button @click="toggleSidebar" class="md:hidden mr-4 text-gray-500 hover:text-primary">
          <i class="fa fa-bars text-xl"></i>
        </button>
        <div class="relative hidden md:block">
          <input type="text" placeholder="    搜索..." class="input pl-10 pr-4 w-64" />
          <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
        </div>
      </div>

      <div class="flex items-center gap-4 sm:gap-6">

        <button
          @click="emit('update:show1', !props.show1)"
          class="btn btn-primary"
        >
          <i class="fa fa-paper-plane mr-2 hidden sm:inline"></i>
          发布任务
        </button>

        <div class="relative">
          <button class="relative text-gray-500 hover:text-primary" @click="toggleDropdown">
            <i class="fa fa-bell text-xl"></i>
            <span
              v-if="notificationStore.count > 0"
              class="absolute -top-1 -right-1 bg-danger text-white text-xs rounded-full w-4 h-4 flex items-center justify-center"
            >
              {{ notificationStore.count }}
            </span>
          </button>

          <div
            v-if="showDropdown"
            class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg py-2 z-50 max-h-80 overflow-auto"
          >
            <div v-if="notificationStore.notifications.length === 0" class="px-4 py-2 text-gray-500 text-sm">
              暂无新通知
            </div>
            <div
              v-for="item in notificationStore.notifications"
              :key="item.id"
              class="px-4 py-3 border-b text-sm flex items-center gap-3 cursor-pointer transition-colors duration-200"
              :class="item.is_read ? 'bg-gray-100 border border-gray-300 text-gray-600 font-normal hover:bg-gray-200' : 'bg-blue-50 border-l-4 border-blue-500 text-blue-900 font-semibold shadow-sm hover:bg-blue-100'"
            >
              <svg v-if="item.is_read" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 flex-shrink-0 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                <circle cx="10" cy="10" r="6" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 flex-shrink-0 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <circle cx="12" cy="12" r="10" stroke-width="2" />
                <circle cx="12" cy="12" r="4" stroke-width="2" />
              </svg>
              <div class="flex flex-col">
                <div>{{ item.message }}</div>
                <div class="text-xs text-gray-400 mt-1">{{ item.timestamp }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="relative group">
          <button class="flex items-center gap-2 text-gray-700 hover:text-primary">
            <img
              :src="userAvatar"
              alt="用户头像"
              class="w-8 h-8 rounded-full object-cover border-2 border-primary"
            />
            <span class="hidden md:inline">{{ userFullName }}</span>
            <i class="fa fa-angle-down hidden sm:inline"></i>
          </button>
          <div
            class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 z-50 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 transform group-hover:translate-y-0 translate-y-2"
          >
            <router-link to="/profile" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">
              <i class="fa fa-user mr-2"></i>个人信息
            </router-link>
<!--            <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">-->
<!--              <i class="fa fa-cog mr-2"></i>账号设置-->
<!--            </a>-->
            <div class="border-t border-gray-200 my-1"></div>
            <a href="#" class="block px-4 py-2 text-gray-700 hover:bg-gray-100" @click.prevent="handleLogout">
              <i class="fa fa-sign-out mr-2"></i>退出登录
            </a>
          </div>
        </div>

      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import authStore from '@/store/auth';
import apiClient from '@/services/api';
import { useNotificationStore } from '@/store/notification';

const props = defineProps({
  userId: Number,
  toggleSidebar: Function,
  show1: Boolean
});
const emit = defineEmits(['update:show1'])




const router = useRouter();
const notificationStore = useNotificationStore();
const showDropdown = ref(false);
const count = ref(0);

const toggleDropdown = async () => {
  showDropdown.value = !showDropdown.value;
  count.value++;
  console.log(props.userId)
  if (count.value % 2 === 0 && props.userId) {
    try {
      // 调用后端接口标记所有通知已读
      await apiClient.put('/notifications/markAllAsRead', {
        userId: props.userId
      });
      // 前端同步已读状态
      notificationStore.markAllAsReadFrontend();
    } catch (error) {
      console.error('标记已读失败', error);
    }
  }
};

const user = computed(() => authStore.currentUser());
const userFullName = computed(() => user.value?.fullName || '访客');
const userAvatar = computed(() => user.value?.avatarUrl || 'https://picsum.photos/id/1005/200/200');

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    authStore.logout();
    router.push('/login');
    alert('已成功退出登录');
  }
};
</script>
