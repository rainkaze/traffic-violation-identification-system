<template>
  <div v-if="isAuthenticated" class="flex h-screen overflow-hidden bg-gray-50 font-sans">
    <Sidebar :is-mobile-open="isSidebarOpen" :toggle-sidebar="toggleSidebar" />
    <div class="flex-1 flex flex-col overflow-hidden">
      <Header :toggle-sidebar="toggleSidebar" :userId="userid" />
      <main class="flex-1 overflow-y-auto bg-gray-50 p-4">
        <input id="text" type="text" />
        <button @click="send()">发送消息</button>
        <button @click="closeWebSocket()">关闭连接</button>
        <button @click="hdfxx">让后端给所有客户端发消息(测试用)</button>
        <div id="message"></div>
        <router-view />
      </main>
    </div>
  </div>
  <div v-else>
    <router-view />
  </div>
</template>

<script setup>
import {ref, watch, computed, onMounted} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import Sidebar from './components/Sidebar.vue';
import Header from './components/Header.vue';
import authStore from './store/auth';
import apiClient from './services/api';
import {jwtDecode} from "jwt-decode";
import {useNotificationStore} from './store/notification';

const notificationStore = useNotificationStore();
const isSidebarOpen = ref(false);
const route = useRoute();
const router = useRouter();
const userid = ref(null);
let websocket = null;




const initMockNotifications = () => {
  // const mockData = [
  //   {
  //     id: 1,
  //     message: '欢迎使用系统！',
  //     timestamp: '2025-06-20 10:00:00',
  //     is_read: false,
  //   },
  //   {
  //     id: 2,
  //     message: '您的订单已发货。',
  //     timestamp: '2025-06-19 15:30:00',
  //     is_read: true,
  //   },
  //   {
  //     id: 3,
  //     message: '系统维护通知，今晚12点开始。',
  //     timestamp: '2025-06-18 18:45:00',
  //     is_read: false,
  //   }
  // ];
  //
  // notificationStore.setNotifications(mockData);
};

// ===============
// 认证相关
// ===============
const isAuthenticated = computed(() => authStore.isAuthenticated());

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value;
};

// 真正从后端获取 userId 并保存
const fetchUserIdFromBackend = async () => {
  try {
    const response = await apiClient.get('/users/getUserId');
    userid.value = response.data; // 通常是 Long 类型
    console.log("用户ID:", userid.value);

    // 有了 userId 再加载通知 & 开启 WebSocket
    await fetchNotifications(userid.value);
    initWebSocket(userid.value);
  } catch (err) {
    console.error("获取 userId 失败", err);
  }
};


// 恢复用户信息
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

// 拉取通知数据
const fetchNotifications = async (userId) => {
  if (!userId) return;
  try {
    const res = await apiClient.get(`/notifications?userId=${userId}`);
    console.log(res.data)
    notificationStore.notifications = res.data;
    notificationStore.count = res.data.filter(n => !n.is_read).length;
  } catch (error) {
    console.error('获取通知失败', error);
  }
};

// WebSocket 相关
const msg = ref();

const initWebSocket = (userId) => {
  if ('WebSocket' in window) {
    websocket = new WebSocket(`ws://localhost:8080/ws/${userId}`);

    websocket.onerror = () => {
      setMessageInnerHTML("error");
    };

    websocket.onopen = () => {
      setMessageInnerHTML("连接成功");
    };

    websocket.onmessage = (event) => {
      const rawData = event.data;
      setMessageInnerHTML(rawData);

      // 构造通知对象
      const notification = {
        id: Date.now(),
        message: rawData,
        timestamp: new Date().toLocaleString(),
        is_read: false,
        userId: userid.value
      };

      notificationStore.addNotification(notification);
      // 发送给后端
      apiClient.post('/notifications/insert', notification)
        .then(() => {
          console.log('通知已发送给后端并成功保存');
        })
        .catch(err => {
          console.error('通知保存失败:', err);
        });
    };



    websocket.onclose = () => {
      setMessageInnerHTML("close");
    };
  } else {
    alert('浏览器不支持 WebSocket');
  }
};

const setMessageInnerHTML = (innerHTML) => {
  const messageDiv = document.getElementById('message');
  if (messageDiv) {
    messageDiv.innerHTML += innerHTML + '<br/>';
  }
};

const send = () => {
  const messageInput = document.getElementById('text');
  if (messageInput && websocket) {
    websocket.send(messageInput.value);
  }
};

const closeWebSocket = () => {
  websocket?.close();
};

const hdfxx = async () => {
  try {
    const response = await apiClient.get('/websocket');
    msg.value = response.data;
    console.log(msg.value);
  } catch (error) {
    console.error('请求失败：', error);
  }
};

// 监听登录状态变化，自动获取通知和开启 websocket
watch(isAuthenticated, async (val) => {
  if (val) {
    userid.value = getUserIdFromToken();
    await fetchNotifications(userid.value);
    initWebSocket(userid.value);
    console.log('userid:', userid.value);
  } else {
    userid.value = null;
    notificationStore.notifications = [];
    notificationStore.count = 0;
    closeWebSocket();
  }
});

// 页面加载时恢复登录并获取通知
onMounted(async () => {
  await checkAuth();
  if (isAuthenticated.value) {
    await fetchUserIdFromBackend();
  }
});

// 关闭 WebSocket 时机
window.onbeforeunload = () => {

  websocket?.close();

};
// 监听登录状态变化，自动获取通知和开启 websocket
watch(isAuthenticated, async (val) => {
  if (val) {
    await fetchUserIdFromBackend();
  } else {
    userid.value = null;
    notificationStore.notifications = [];
    notificationStore.count = 0;
    closeWebSocket();
  }
});



watch(route, () => {
  if (isSidebarOpen.value) {
    isSidebarOpen.value = false;
  }
});

</script>

<style>
/* 你的样式 */
</style>
