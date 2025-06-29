import { reactive, readonly } from 'vue';
import apiClient from '@/services/api';

const state = reactive({
  user: null,
  token: localStorage.getItem('token') || null,
});

const mutations = {
  setUser(userData) {
    state.user = userData;
  },
  setToken(token) {
    state.token = token;
    if (token) {
      localStorage.setItem('token', token);
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      localStorage.removeItem('token');
      delete apiClient.defaults.headers.common['Authorization'];
    }
  },
  clearLocalState() {
    state.user = null;
    state.token = null;
    localStorage.removeItem('token');
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

const actions = {
  async login(credentials) {
    // ...
  },

  // ======== 主要修改点：在 finally 块中添加页面跳转 ========
  async logoutUser() {
    try {
      await apiClient.post('/auth/logout');
      console.log("Backend logout API was called successfully.");
    } catch (error) {
      console.error("Error calling backend logout, proceeding with local cleanup:", error);
    } finally {
      // 清理前端的状态
      mutations.clearLocalState();

      // **新增代码：强制页面跳转到登录页**
      // 使用 window.location.href 可以确保在任何地方调用登出都能正确跳转
      window.location.href = '/login';
    }
  }
};

const getters = {
  isAuthenticated: () => !!state.token,
  currentUser: () => state.user,
  isAdmin: () => state.user && state.user.rank === '管理员',
};

export default {
  state: readonly(state),
  setUser: mutations.setUser,
  setToken: mutations.setToken,
  logout: actions.logoutUser,
  isAuthenticated: getters.isAuthenticated,
  isAdmin: getters.isAdmin,
  currentUser: getters.currentUser,
};
