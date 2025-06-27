<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">设备管理</h2>
      <p class="text-gray-600">克拉玛依市交通监控设备状态与配置</p>
    </div>

    <div class="card mb-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
        <div class="flex flex-col sm:flex-row gap-3">
          <div class="relative">
            <input type="text" v-model="searchTerm" placeholder="搜索设备ID或名称" class="input pl-10 w-full sm:w-64">
            <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
          <select v-model="selectedType" class="input w-full sm:w-40">
            <option value="">全部类型</option>
            <option v-for="type in deviceTypes" :key="type" :value="type">{{ type }}</option>
          </select>
          <select v-model="selectedStatus" class="input w-full sm:w-40">
            <option value="">全部状态</option>
            <option value="online">在线</option>
            <option value="offline">离线</option>
            <option value="warning">警告</option>
            <option value="maintenance">维护中</option>
          </select>
        </div>
        <div class="flex gap-2">
          <router-link to="/devices/new" class="btn btn-primary">
            <i class="fa fa-plus mr-1"></i> 添加设备
          </router-link>
        </div>
      </div>

      <div v-if="isLoading" class="text-center py-10">加载设备列表中...</div>
      <div v-else-if="filteredDevices.length === 0" class="text-center py-10">未找到匹配的设备</div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="device in filteredDevices" :key="device.deviceId" class="card border-l-4" :class="statusBorderColor(device.status)">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="font-bold text-gray-800">{{ device.deviceCode }}</h3>
              <p class="text-sm text-gray-600 truncate" :title="device.address">{{ device.address }}</p>
            </div>
            <span class="badge text-white" :class="statusBgColor(device.status)">{{ device.status }}</span>
          </div>
          <div class="mt-4 pt-3 border-t text-sm text-gray-600 space-y-1">
            <p>类型: <span class="font-medium text-gray-800">{{ device.deviceType }}</span></p>
            <p>型号: <span class="font-medium text-gray-800">{{ device.modelName || 'N/A' }}</span></p>
            <p>IP: <span class="font-medium text-gray-800">{{ device.ipAddress || 'N/A' }}</span></p>
          </div>
          <div class="mt-4 pt-3 border-t flex justify-end gap-2">
            <router-link :to="`/devices/${device.deviceId}`" class="btn btn-secondary text-xs">编辑</router-link>
            <button @click="deleteDevice(device.deviceId)" class="btn btn-danger text-xs">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <h3 class="font-bold text-gray-800 mb-4">设备统计</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="h-64"><canvas ref="deviceStatusChart"></canvas></div>
        <div class="h-64"><canvas ref="deviceTypeChart"></canvas></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onActivated } from 'vue';
import Chart from 'chart.js/auto';
import apiClient from '@/services/api';

const devices = ref([]);
const isLoading = ref(true);
const searchTerm = ref('');
const selectedType = ref('');
const selectedStatus = ref('');

const deviceStatusChart = ref(null);
const deviceTypeChart = ref(null);
let statusChartInstance = null;
let typeChartInstance = null;

const deviceTypes = computed(() => {
  return [...new Set(devices.value.map(d => d.deviceType))];
});

const filteredDevices = computed(() => {
  return devices.value.filter(device => {
    const term = searchTerm.value.toLowerCase();
    const matchTerm = device.deviceCode.toLowerCase().includes(term) || device.deviceName.toLowerCase().includes(term);
    const matchType = !selectedType.value || device.deviceType === selectedType.value;
    const matchStatus = !selectedStatus.value || device.status === selectedStatus.value;
    return matchTerm && matchType && matchStatus;
  });
});

const statusBorderColor = (status) => {
  const map = { online: 'border-success', offline: 'border-danger', warning: 'border-warning', maintenance: 'border-gray-400' };
  return map[status] || 'border-gray-300';
};
const statusBgColor = (status) => {
  const map = { online: 'bg-success', offline: 'bg-danger', warning: 'bg-warning', maintenance: 'bg-gray-400' };
  return map[status] || 'bg-gray-300';
};

const fetchDevices = async () => {
  isLoading.value = true;
  try {
    const response = await apiClient.get('/devices');
    devices.value = response.data;
  } catch (error) {
    console.error("加载设备列表失败:", error);
  } finally {
    isLoading.value = false;
  }
};

const deleteDevice = async (id) => {
  if (confirm(`确定要删除设备 #${id} 吗？`)) {
    try {
      await apiClient.delete(`/devices/${id}`);
      alert('设备删除成功！');
      await fetchDevices(); // 重新加载列表和图表
      await fetchChartData();
    } catch (error) {
      alert('删除失败：' + (error.response?.data?.message || '未知错误'));
    }
  }
};

const fetchChartData = async () => {
  try {
    const [statusRes, typeRes] = await Promise.all([
      apiClient.get('/devices/statistics/status'),
      apiClient.get('/devices/statistics/type')
    ]);
    renderStatusChart(statusRes.data);
    renderTypeChart(typeRes.data);
  } catch (error) {
    console.error("加载图表数据失败:", error);
  }
};

const renderStatusChart = (data) => {
  if (statusChartInstance) statusChartInstance.destroy();
  if (!deviceStatusChart.value) return;

  const statusColors = {
    online: 'rgba(16, 185, 129, 0.8)',
    offline: 'rgba(239, 68, 68, 0.8)',
    warning: 'rgba(245, 158, 11, 0.8)',
    maintenance: 'rgba(107, 114, 128, 0.8)'
  };

  statusChartInstance = new Chart(deviceStatusChart.value.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: data.map(d => d.label),
      datasets: [{
        data: data.map(d => d.value),
        backgroundColor: data.map(d => statusColors[d.label] || '#cccccc')
      }]
    },
    options: { responsive: true, maintainAspectRatio: false, plugins: { title: { display: true, text: '设备状态分布' } } }
  });
};

const renderTypeChart = (data) => {
  if (typeChartInstance) typeChartInstance.destroy();
  if (!deviceTypeChart.value) return;

  typeChartInstance = new Chart(deviceTypeChart.value.getContext('2d'), {
    type: 'bar',
    data: {
      labels: data.map(d => d.label),
      datasets: [{
        label: '设备数量',
        data: data.map(d => d.value),
        backgroundColor: 'rgba(30, 64, 175, 0.8)'
      }]
    },
    options: { responsive: true, maintainAspectRatio: false, plugins: { title: { display: true, text: '设备类型分布' } } }
  });
};

onMounted(() => {
  fetchDevices();
  fetchChartData();
});
onActivated(() => {
  fetchDevices();
  fetchChartData();
});
</script>
