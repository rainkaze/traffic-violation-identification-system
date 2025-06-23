<template>
  <div v-if="isAuthenticated" class="flex h-screen overflow-hidden bg-gray-50 font-sans">
    <Sidebar :is-mobile-open="isSidebarOpen" :toggle-sidebar="toggleSidebar" />
    <div class="flex-1 flex flex-col overflow-hidden">
      <Header :toggle-sidebar="toggleSidebar" :userId="userid"  v-model:show1="show1"/>
      <div
        v-if="show1"
        class="fixed top-20 right-10 w-[500px] bg-white border border-gray-200 rounded-lg shadow-lg z-50 p-6"
      >
        <!-- 标题栏 -->
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-gray-800">发送新任务</h3>
          <button @click="show1 = false" class="text-gray-400 hover:text-gray-600">
            <i class="fa fa-times"></i>
          </button>
        </div>

        <!-- 收件人选择 -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">收件人</label>

          <!-- 搜索框 -->
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="输入用户名进行搜索..."
            class="w-full border rounded px-3 py-2 mb-2 focus:outline-none focus:ring focus:ring-blue-200"
            @focus="fetchAllUsers"
          />

          <!-- 全选开关 -->
          <div class="flex items-center gap-2 mb-2">
            <input type="checkbox" :checked="allSelected" @change="toggleSelectAll" />
            <span class="text-sm text-gray-700">全选当前列表</span>
          </div>

          <!-- 用户列表 -->
          <div class="max-h-32 overflow-y-auto border rounded p-2">
            <div
              v-for="user in userList"
              :key="user.userId"
              class="flex items-center gap-2 mb-1"
            >
              <input
                type="checkbox"
                :id="'user_' + user.userId"
                v-model="selectedUsers"
                :value="user.userId"
              />
              <label :for="'user_' + user.userId">
                {{ user.username }}（ID: {{ user.userId }}）
              </label>
            </div>
          </div>

        </div>




        <!-- 主题 -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">主题</label>
          <input
            type="text"
            v-model="subject"
            placeholder="请输入通知主题"
            class="w-full border rounded px-3 py-2 focus:outline-none focus:ring focus:ring-blue-200"
          />
        </div>

        <!-- 正文 -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">内容</label>
          <textarea
            v-model="message"
            placeholder="请输入通知内容"
            class="w-full border rounded px-3 py-2 h-28 resize-none focus:outline-none focus:ring focus:ring-blue-200"
          ></textarea>
        </div>

        <!-- 发送按钮 -->
        <button
          @click="sendNotification"
          class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition"
        >
          发布任务
        </button>
      </div>





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
import debounce from 'lodash/debounce'
import { ElMessage } from 'element-plus'

import { useSystemConfigStore } from '@/store/systemConfig'
const configStore = useSystemConfigStore()

onMounted(() => {
  configStore.fetchConfig()
})


const searchKeyword = ref('')
// const userList = ref([])
const userList= ref([
  // {
  //   "id": 101,
  //   "name": "张三"
  // },
  // {
  //   "id": 102,
  //   "name": "李四"
  // },
  // {
  //   "id": 103,
  //   "name": "王五"
  // },
  // {
  //   "id": 104,
  //   "name": "赵六"
  // },
  // {
  //   "id": 105,
  //   "name": "小红"
  // },
  // {
  //   "id": 106,
  //   "name": "小明"
  // }
])


const notificationStore = useNotificationStore();

const isSidebarOpen = ref(false);
const route = useRoute();
const router = useRouter();
const userid = ref(null);
let websocket = null;
const show1 = ref(false);
const subject = ref('');
const message = ref('');
//选中的用户数组
const selectedUsers = ref([])


const sendNotification = async () => {
  if (!subject.value.trim() || !message.value.trim() || selectedUsers.value.length === 0)
  {
    ElMessage.warning('请填写主题、内容并选择至少一个收件人')
    return;
  }

  const payload = {
    subject: subject.value.trim(),
    message: message.value.trim(),
    recipientIds: selectedUsers.value,
  };

  console.log( payload)
  try {
    const response = await apiClient.post('/notifications/send', payload);
    ElMessage.success('通知发送成功！')

    // 清空表单
    subject.value = '';
    message.value = '';
    selectedUsers.value = [];

    // 关闭弹窗（如果有控制）
    show1.value = false;
  } catch (error) {
    console.error("通知发送失败", error);
    ElMessage.error('发送失败，请稍后重试')
  }
};


// 模糊搜索函数（后端接口支持 ?keyword=xxx）
const fetchUsers = async (keyword) => {
  try {
    const res = await apiClient.get('/users/search', {
      params: { keyword }
    })
    userList.value = res.data
  } catch (err) {
    console.error('用户搜索失败', err)
  }
}

//给搜索框绑定的！！！
const fetchAllUsers = async () => {
  try {
    const res = await apiClient.get('/users/search'); // 所有用户
    userList.value = res.data;
  } catch (err) {
    console.error('获取所有用户失败', err);
  }
};


// 防抖版搜索函数（避免频繁请求）
const debouncedFetchUsers = debounce(fetchUsers, 300)

// 实时监听输入
watch(searchKeyword, (val) => {
  debouncedFetchUsers(val)
})


// 全选判断逻辑
const allSelected = computed(() =>
  userList.value.length > 0 &&
  userList.value.every(user => selectedUsers.value.includes(user.userId))
);

// 全选/取消选择
const toggleSelectAll = () => {
  if (allSelected.value) {
    // 取消全选：移除当前 userList 中的用户
    selectedUsers.value = selectedUsers.value.filter(
      id => !userList.value.some(user => user.userId === id)
    );
  } else {
    // 添加所有 userList 中的 userId（去重）
    const idsToAdd = userList.value.map(user => user.userId);
    selectedUsers.value = Array.from(new Set([...selectedUsers.value, ...idsToAdd]));
  }
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
    console.log(show1.value);
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
  fetchAllUsers()
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
