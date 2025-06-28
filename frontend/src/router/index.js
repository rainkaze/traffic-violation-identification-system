import { createRouter, createWebHashHistory } from 'vue-router';
import authStore from '@/store/auth';

// 1. 在顶部一次性导入所有组件
import DashboardView from '../views/DashboardView.vue';
import ViolationsView from '../views/ViolationsView.vue';
import MonitoringView from '../views/MonitoringView.vue';
import EnforcementView from '../views/EnforcementView.vue';
import AccidentsView from '../views/AccidentsView.vue';
import StatisticsView from '../views/StatisticsView.vue';
import DevicesView from '../views/DevicesView.vue';
import SettingsView from '../views/SettingsView.vue';
import HelpView from '../views/HelpView.vue';
import ProfileView from '../views/ProfileView.vue';

// --- 新增页面导入 ---
import LoginView from '../views/auth/LoginView.vue';
import RegisterView from '../views/auth/RegisterView.vue';
import UserManagementView from '../views/admin/UserManagementView.vue';
import WorkflowManagementView from '../views/admin/WorkflowManagementView.vue';
import WorkflowForm from '../views/admin/WorkflowForm.vue';
import DeviceFormView from '../views/DeviceFormView.vue'; // 新增导入


const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    // --- 新增认证路由 ---
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true } // 只有游客可以访问
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { requiresGuest: true }
    },

    // --- 应用主路由 ---
    { path: '/', redirect: '/dashboard' },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/violations',
      name: 'violations',
      component: ViolationsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/monitoring',
      name: 'monitoring',
      component: MonitoringView,
      meta: { requiresAuth: true }
    }, // --- 新增监控详情页路由 ---
    {
      path: '/monitoring/:id',
      name: 'monitoring-detail',
      component: () => import('../views/MonitoringDetailView.vue'), // 懒加载方式导入
      props: true, // 允许将路由参数:id作为props传入组件
      meta: { requiresAuth: true }
    },
    {
      path: '/enforcement',
      name: 'enforcement',
      component: EnforcementView,
      meta: { requiresAuth: true }
    },
    {
      path: '/accidents',
      name: 'accidents',
      component: AccidentsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: StatisticsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/devices',
      name: 'devices',
      component: DevicesView,
      meta: { requiresAuth: true }
    },// --- 新增设备表单路由 ---
    {
      path: '/devices/new',
      name: 'device-new',
      component: DeviceFormView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/devices/:id',
      name: 'device-edit',
      component: DeviceFormView,
      props: true, // 允许将路由参数id作为props传入组件
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/settings',
      name: 'settings',
      component: SettingsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/help',
      name: 'help',
      component: HelpView,
      meta: { requiresAuth: true }
    },
    // --- 个人信息路由 ---
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true } // 需要登录才能访问
    },
    // --- 管理员路由 ---
    {
      path: '/user-management',
      name: 'user-management',
      component: UserManagementView,
      meta: { requiresAuth: true, requiresAdmin: true } // 需要登录且是管理员
    },
    // --- 工作流路由 ---
    {
      path: '/workflow-management',
      name: 'workflow-management',
      component: WorkflowManagementView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { // --- 新建工作流的专属页面 ---
      path: '/workflow-management/new',
      name: 'workflow-form-new',
      component: WorkflowForm,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    { // --- 编辑页面  ---
      path: '/workflow-management/edit/:id',
      name: 'workflow-form-edit',
      component: WorkflowForm,
      props: true, // 允许将路由参数作为props传入组件
      meta: { requiresAuth: true, requiresAdmin: true }
    }, // 2. 添加新路由
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ],
});


// --- 路由守卫 ---
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);

  const isAuthenticated = authStore.isAuthenticated();
  const isAdmin = authStore.isAdmin();

  if (requiresAuth && !isAuthenticated) {
    // 此路由需要认证，但用户未登录
    next({ name: 'login' });
  } else if (requiresAdmin && !isAdmin) {
    // 此路由需要管理员权限，但用户不是管理员
    alert('您没有权限访问此页面。');
    next({ name: 'dashboard' }); // 或重定向到无权限页面
  } else if (requiresGuest && isAuthenticated) {
    // 此路由只允许游客访问，但用户已登录
    next({ name: 'dashboard' });
  } else {
    // 其他情况，正常放行
    next();
  }
});


export default router;
