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
            <input type="text" v-model="searchTerm" placeholder="   搜索设备ID或名称" class="input pl-10 w-full sm:w-64">
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
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div v-for="device in filteredDevices" :key="device.deviceId"
             class="card flex flex-col justify-between border-l-4 transition-all hover:shadow-lg hover:-translate-y-1"
             :class="statusBorderColor(device.status)">

          <div>
            <div class="flex justify-between items-start mb-3">
              <h3 class="font-bold text-lg text-gray-800 pr-2">{{ device.deviceName }}</h3>
              <span class="badge text-white text-xs flex-shrink-0" :class="statusBgColor(device.status)">{{ device.status }}</span>
            </div>

            <div class="text-sm text-gray-600 space-y-2">
              <p class="flex items-center" title="设备编码">
                <i class="fa fa-barcode w-5 text-center mr-2 text-gray-400"></i>
                <span class="font-medium text-gray-800">{{ device.deviceCode }}</span>
              </p>
              <p class="flex items-center" title="设备类型">
                <i class="fa fa-sitemap w-5 text-center mr-2 text-gray-400"></i>
                <span class="font-medium text-gray-800">{{ device.deviceType }}</span>
              </p>
              <p class="flex items-start" title="安装地址">
                <i class="fa fa-map-marker-alt w-5 text-center mr-2 mt-1 text-gray-400"></i>
                <span class="font-medium text-gray-800">{{ device.address }}</span>
              </p>
              <p class="flex items-center" title="所属辖区">
                <i class="fa fa-building w-5 text-center mr-2 text-gray-400"></i>
                <span class="font-medium text-gray-800">{{ device.jurisdiction || 'N/A' }}</span>
              </p>
              <p class="flex items-start" title="RTSP 地址">
                <i class="fa fa-video w-5 text-center mr-2 mt-1 text-gray-400"></i>
                <span class="font-medium text-gray-800 break-all">{{ device.rtspUrl || 'N/A' }}</span>
              </p>
            </div>
          </div>

          <div class="mt-4 pt-3 border-t flex justify-end gap-2">
            <router-link :to="`/devices/${device.deviceId}`" class="btn btn-secondary text-xs">
              <i class="fa fa-edit mr-1"></i>编辑
            </router-link>
            <button @click="deleteDevice(device.deviceId)" class="btn btn-danger text-xs">
              <i class="fa fa-trash-alt mr-1"></i>删除
            </button>
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
import { ref, onMounted, computed, onActivated, nextTick } from 'vue'; // 引入 nextTick
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
    // 确保 deviceName 存在，避免null错误
    const deviceName = device.deviceName || '';
    const matchTerm = device.deviceCode.toLowerCase().includes(term) || deviceName.toLowerCase().includes(term);
    const matchType = !selectedType.value || device.deviceType === selectedType.value;
    const matchStatus = !selectedStatus.value || device.status === selectedStatus.value;
    return matchTerm && matchType && matchStatus;
  });
});

const statusBorderColor = (status) => {
  const map = { online: 'border-success', offline: 'border-danger', warning: 'border-warning', maintenance: 'border-gray-400' };
  return map[status.toLowerCase()] || 'border-gray-300';
};

const statusBgColor = (status) => {
  const map = { online: 'bg-success', offline: 'bg-danger', warning: 'bg-warning', maintenance: 'bg-gray-400' };
  return map[status.toLowerCase()] || 'bg-gray-300';
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
      // 重新加载列表和图表数据
      await Promise.all([fetchDevices(), fetchChartData()]);
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
    // [优化] 使用 nextTick 确保DOM更新后再渲染图表
    await nextTick();
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
        backgroundColor: data.map(d => statusColors[d.label.toLowerCase()] || '#cccccc')
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

// 合并 onMounted 和 onActivated 的逻辑
const loadData = () => {
  fetchDevices();
  fetchChartData();
}

onMounted(loadData);
onActivated(loadData);
</script>
