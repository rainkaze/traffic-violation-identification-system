<template>
  <div v-if="isAuthenticated" class="flex h-screen overflow-hidden bg-gray-50 font-sans">
    <Sidebar :is-mobile-open="isSidebarOpen" :toggle-sidebar="toggleSidebar" />

    <div class="flex-1 flex flex-col overflow-hidden">
      <Header :toggle-sidebar="toggleSidebar" :userId="userid" v-model:show1="show1" />

      <div v-if="show1" class="fixed top-20 right-10 w-[500px] bg-white border border-gray-200 rounded-lg shadow-lg z-50 p-6">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-gray-800">发送新任务</h3>
          <button @click="show1 = false" class="text-gray-400 hover:text-gray-600">
            <i class="fa fa-times"></i>
          </button>
        </div>

        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">收件人</label>
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="输入用户名进行搜索..."
            class="w-full border rounded px-3 py-2 mb-2 focus:outline-none focus:ring focus:ring-blue-200"
            @focus="fetchAllUsers"
          />
          <div class="flex items-center gap-2 mb-2">
            <input type="checkbox" :checked="allSelected" @change="toggleSelectAll" />
            <span class="text-sm text-gray-700">全选当前列表</span>
          </div>
          <div class="max-h-32 overflow-y-auto border rounded p-2">
            <div v-for="user in userList" :key="user.userId" class="flex items-center gap-2 mb-1">
              <input type="checkbox" :id="'user_' + user.userId" v-model="selectedUsers" :value="user.userId" />
              <label :for="'user_' + user.userId">{{ user.username }}（ID: {{ user.userId }}）</label>
            </div>
          </div>
        </div>

        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">主题</label>
          <input
            type="text"
            v-model="subject"
            placeholder="请输入通知主题"
            class="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:ring-blue-200"
          />
        </div>

        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">内容</label>
          <textarea
            v-model="message"
            placeholder="请输入通知内容"
            class="w-full border rounded px-3 py-2 h-28 resize-none focus:outline-none focus:ring focus:ring-blue-200"
          ></textarea>
        </div>

        <button @click="sendNotification" class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition">
          发布任务
        </button>
      </div>

      <main class="flex-1 overflow-y-auto bg-gray-50 p-4">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
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
import { jwtDecode } from "jwt-decode";
import debounce from 'lodash/debounce';
import { ElMessage } from 'element-plus';

// 导入组件
import Sidebar from './components/Sidebar.vue';
import Header from './components/Header.vue';

// 导入状态管理和API客户端
import authStore from './store/auth';
import { useNotificationStore } from './store/notification';
import { useSystemConfigStore } from '@/store/systemConfig';
import apiClient from './services/api';

// --- 状态定义 ---
const isSidebarOpen = ref(false);
const show1 = ref(false);
const userid = ref(null);
let websocket = null;
const searchKeyword = ref('');
const userList = ref([]);
const selectedUsers = ref([]);
const subject = ref('');
const message = ref('');

// Pinia Stores & Vue Router
const notificationStore = useNotificationStore();
const configStore = useSystemConfigStore();
const route = useRoute();
const router = useRouter();


// --- 计算属性 ---
const isAuthenticated = computed(() => authStore.isAuthenticated());
const allSelected = computed(() =>
  userList.value.length > 0 &&
  userList.value.every(user => selectedUsers.value.includes(user.userId))
);


// --- 方法 ---

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

/**
 * [ 核心修改 ]
 * 此方法现在只负责在页面加载时恢复用户信息，并启动后续流程
 */
const restoreUserSession = async () => {
  const token = authStore.state.token;
  if (token && !authStore.currentUser()) {
    try {
      // 通过 /me 接口一步到位获取当前用户信息
      const response = await apiClient.get('/users/me');
      const user = response.data;
      authStore.setUser(user); // 保存完整用户信息到store
      userid.value = user.userId; // 设置当前用户ID

      // 成功获取用户信息后，再加载通知和开启WebSocket
      await fetchNotifications(userid.value);
      initWebSocket(userid.value);
    } catch (error) {
      console.error("Session restore failed:", error);
      authStore.logout();
      router.push('/login');
    }
  } else if (authStore.currentUser()) {
    // 如果用户信息已存在，也确保加载通知和WebSocket
    userid.value = authStore.currentUser().userId;
    await fetchNotifications(userid.value);
    initWebSocket(userid.value);
  }
};

const fetchAllUsers = async () => {
  if (userList.value.length > 0 && !searchKeyword.value) return;
  try {
    const res = await apiClient.get('/users/search');
    userList.value = res.data;
  } catch (err) {
    console.error('获取所有用户失败', err);
  }
};

const fetchUsersByKeyword = async (keyword) => {
  try {
    const res = await apiClient.get('/users/search', { params: { keyword } });
    userList.value = res.data;
  } catch (err) {
    console.error('用户搜索失败', err);
  }
};

const debouncedFetchUsers = debounce(fetchUsersByKeyword, 300);

const toggleSelectAll = () => {
  if (allSelected.value) {
    const currentUserIds = userList.value.map(user => user.userId);
    selectedUsers.value = selectedUsers.value.filter(id => !currentUserIds.includes(id));
  } else {
    const idsToAdd = userList.value.map(user => user.userId);
    selectedUsers.value = Array.from(new Set([...selectedUsers.value, ...idsToAdd]));
  }
};

const sendNotification = async () => {
  if (!subject.value.trim() || !message.value.trim() || selectedUsers.value.length === 0) {
    ElMessage.warning('请填写主题、内容并选择至少一个收件人');
    return;
  }
  const payload = {
    subject: subject.value.trim(),
    message: message.value.trim(),
    recipientIds: selectedUsers.value,
  };
  try {
    await apiClient.post('/notifications/send', payload);
    ElMessage.success('任务发布成功！');
    show1.value = false;
    subject.value = '';
    message.value = '';
    selectedUsers.value = [];
  } catch (error) {
    console.error("任务发布失败", error);
    ElMessage.error('发布失败，请稍后重试');
  }
};

const fetchNotifications = async (userId) => {
  if (!userId) return;
  try {
    const res = await apiClient.get(`/notifications?userId=${userId}`);
    notificationStore.notifications = res.data;
    notificationStore.count = res.data.filter(n => !n.is_read).length;
  } catch (error) {
    console.error('获取通知失败', error);
  }
};

const initWebSocket = (userId) => {
  if (!userId || websocket) return;
  if ('WebSocket' in window) {
    websocket = new WebSocket(`ws://localhost:8080/ws/${userId}`);
    websocket.onopen = () => console.log("WebSocket连接成功");
    websocket.onerror = () => console.error("WebSocket连接错误");
    websocket.onclose = () => {
      console.log("WebSocket连接关闭");
      websocket = null;
    };
    websocket.onmessage = (event) => {
      const rawData = event.data;
      const notification = {
        id: Date.now(),
        message: rawData,
        timestamp: new Date().toLocaleString(),
        is_read: false,
        userId: userid.value
      };
      notificationStore.addNotification(notification);
      apiClient.post('/notifications/insert', notification)
        .catch(err => console.error('通知保存失败:', err));
    };
  } else {
    alert('您的浏览器不支持 WebSocket');
  }
};

const closeWebSocket = () => {
  websocket?.close();
};

// --- Watchers ---
watch(route, () => {
  if (isSidebarOpen.value) {
    isSidebarOpen.value = false;
  }
});

watch(searchKeyword, (val) => {
  debouncedFetchUsers(val);
});

// --- Lifecycle Hooks ---
onMounted(async () => {
  await restoreUserSession();
  configStore.fetchConfig();
});

window.onbeforeunload = closeWebSocket;
</script>
<style>
/* 全局样式现在由 main.css 控制 */
</style>
