import { createRouter, createWebHashHistory } from 'vue-router';

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

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/dashboard' },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView, // 直接使用导入的组件
    },
    {
      path: '/violations',
      name: 'violations',
      component: ViolationsView, // 直接使用导入的组件
    },
    {
      path: '/monitoring',
      name: 'monitoring',
      component: MonitoringView,
    },
    {
      path: '/enforcement',
      name: 'enforcement',
      component: EnforcementView,
    },
    {
      path: '/accidents',
      name: 'accidents',
      component: AccidentsView,
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: StatisticsView,
    },
    {
      path: '/devices',
      name: 'devices',
      component: DevicesView,
    },
    {
      path: '/settings',
      name: 'settings',
      component: SettingsView,
    },
    {
      path: '/help',
      name: 'help',
      component: HelpView,
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ],
});

export default router;
