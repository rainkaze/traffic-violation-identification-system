import axios from 'axios';
import authStore from '@/store/auth';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(config => {
  const token = authStore.state.token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// Optional: Add a response interceptor to handle 401 errors globally
apiClient.interceptors.response.use(response => response, error => {
  if (error.response && error.response.status === 401) {
    authStore.logout();
    // Using window.location to force a reload and clear state.
    // In a larger app, you might use the router instance.
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

export default apiClient;
