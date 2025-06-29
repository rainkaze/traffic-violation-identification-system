// store/notification.js
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([]);
  const count = ref(0);

  // 新增通知，默认未读，放最前面，更新未读计数
  function addNotification(notification) {
    // 如果没传 is_read 字段，默认设为 false
    if (typeof notification.is_read !== 'boolean') {
      notification.is_read = false;
    }
    notifications.value.unshift(notification);
    updateCount();
  }

  // 通过遍历计算未读数量
  function updateCount() {
    count.value = notifications.value.filter(item => !item.is_read).length;
  }

  // 标记所有通知为已读（前端），同时更新未读数
  function markAllAsReadFrontend() {
    notifications.value.forEach(item => {
      item.is_read = true;
    });
    updateCount();
  }

  // 从后端加载通知列表并替换当前数据，同时更新未读数
  function setNotifications(newNotifications) {
    notifications.value = newNotifications || [];
    updateCount();
  }

  return {
    notifications,
    count,
    addNotification,
    markAllAsReadFrontend,
    setNotifications,
    updateCount
  };
});
