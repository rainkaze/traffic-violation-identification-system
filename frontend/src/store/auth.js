import { reactive, readonly } from 'vue';
import { useRouter } from 'vue-router';

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
    } else {
      localStorage.removeItem('token');
    }
  },
  logout() {
    state.user = null;
    state.token = null;
    localStorage.removeItem('token');
  }
};

const actions = {
  async login(credentials) {
    // NOTE: The actual API call will be in the component.
    // This is a placeholder for where login logic could go in a more complex app.
    // For now, we'll just set the token and user from the component.
  },
  logoutUser() {
    mutations.logout();
    // No router access here, handle redirect in component or router guard.
  }
};

const getters = {
  isAuthenticated: () => !!state.token,
  currentUser: () => state.user,
  isAdmin: () => state.user && state.user.rank === '管理员',
};

// Export a simplified store object
export default {
  state: readonly(state),
  setUser: mutations.setUser,
  setToken: mutations.setToken,
  logout: actions.logoutUser,
  isAuthenticated: getters.isAuthenticated,
  isAdmin: getters.isAdmin,
  currentUser: getters.currentUser,
};
