<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">仪表盘</h2>
      <p class="text-gray-600">克拉玛依市交通违法实时概览</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
      <div class="card bg-gradient-to-r from-primary to-secondary text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium opacity-90">今日违法总数</p>
            <h3 class="text-3xl font-bold mt-1">{{ stats.totalToday }}</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa mr-1" :class="stats.totalChange >= 0 ? 'fa-arrow-up text-green-300' : 'fa-arrow-down text-red-300'"></i>
              <span :class="stats.totalChange >= 0 ? 'text-green-300' : 'text-red-300'">
                {{ stats.totalChange.toFixed(0) }}% 较昨日
              </span>
            </p>
          </div>
          <div class="bg-white/20 p-3 rounded-lg">
            <i class="fa fa-exclamation-triangle text-xl"></i>
          </div>
        </div>
      </div>
      <div class="card bg-gradient-to-r from-success to-teal-400 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium opacity-90">已处理违法</p>
            <h3 class="text-3xl font-bold mt-1">{{ stats.processedToday }}</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa mr-1" :class="stats.processedChange >= 0 ? 'fa-arrow-up text-green-300' : 'fa-arrow-down text-red-300'"></i>
              <span :class="stats.processedChange >= 0 ? 'text-green-300' : 'text-red-300'">
                {{ stats.processedChange.toFixed(0) }}% 较昨日
              </span>
            </p>
          </div>
          <div class="bg-white/20 p-3 rounded-lg">
            <i class="fa fa-check-circle text-xl"></i>
          </div>
        </div>
      </div>
      <div class="card bg-gradient-to-r from-warning to-orange-400 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium opacity-90">待处理违法</p>
            <h3 class="text-3xl font-bold mt-1">{{ stats.pendingToday }}</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa mr-1" :class="stats.pendingChange >= 0 ? 'fa-arrow-up text-red-400' : 'fa-arrow-down text-green-400'"></i>
              <span :class="stats.pendingChange >= 0 ? 'text-red-400' : 'text-green-400'">
                {{ stats.pendingChange.toFixed(0) }}% 较昨日
              </span>
            </p>
          </div>
          <div class="bg-white/20 p-3 rounded-lg">
            <i class="fa fa-clock-o text-xl"></i>
          </div>
        </div>
      </div>
      <div class="card bg-gradient-to-r from-danger to-red-400 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium opacity-90">严重违法行为</p>
            <h3 class="text-3xl font-bold mt-1">{{ stats.seriousToday }}</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa mr-1" :class="stats.seriousChange >= 0 ? 'fa-arrow-up text-red-400' : 'fa-arrow-down text-green-300'"></i>
              <span :class="stats.seriousChange >= 0 ? 'text-red-300' : 'text-green-300'">
                {{ stats.seriousChange.toFixed(0) }}% 较昨日
              </span>
            </p>
          </div>
          <div class="bg-white/20 p-3 rounded-lg">
            <i class="fa fa-exclamation-circle text-xl"></i>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="card lg:col-span-2">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">违法类型分布</h3>
          <div class="flex space-x-2">
            <button @click="fetchChartData('today')" :class="['btn text-sm', timeRange === 'today' ? 'btn-primary' : 'btn-secondary']">今日</button>
            <button @click="fetchChartData('week')" :class="['btn text-sm', timeRange === 'week' ? 'btn-primary' : 'btn-secondary']">本周</button>
            <button @click="fetchChartData('month')" :class="['btn text-sm', timeRange === 'month' ? 'btn-primary' : 'btn-secondary']">本月</button>
          </div>
        </div>
        <div class="h-80"><canvas ref="violationTypeChart"></canvas></div>
      </div>

      <div class="card">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">实时预警</h3>
          <router-link to="/violations" class="text-primary hover:text-primary/80 text-sm">查看全部</router-link>
        </div>
        <div class="h-80 overflow-y-auto space-y-3">
          <div v-for="(warning, index) in realtimeWarnings" :key="index" class="flex items-center gap-3 p-2 rounded-lg"
               :class="warning.warningLevel === 1 ? 'bg-red-50 border-l-4 border-danger' : 'bg-yellow-50 border-l-4 border-warning'">
            <i class="fa fa-car text-lg" :class="warning.warningLevel === 1 ? 'text-danger' : 'text-warning'"></i>
            <div>
              <p class="font-medium text-sm">{{ warning.plateNumber }} {{ warning.violationType }}</p>
              <p class="text-xs text-gray-500">{{ warning.timeAgo }} @ {{ warning.location }}</p>
            </div>
            <span class="ml-auto text-xs font-bold" :class="warning.warningLevel === 1 ? 'text-danger' : 'text-warning'">
              {{ warning.warningLevel === 1 ? '一级预警' : '二级预警' }}
            </span>
          </div>
        </div>
      </div>

      <div class="card lg:col-span-3">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">最近违法记录</h3>
          <router-link to="/violations" class="text-primary hover:text-primary/80 flex items-center">
            <span>查看全部</span><i class="fa fa-angle-right ml-1"></i>
          </router-link>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
              <th class="table-header">时间</th>
              <th class="table-header">车牌号码</th>
              <th class="table-header">违法类型</th>
              <th class="table-header">地点</th>
              <th class="table-header">状态</th>
            </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="(item, index) in recentViolations" :key="index" class="hover:bg-gray-50">
              <td class="table-cell">{{ formatTime(item.time) }}</td>
              <td class="table-cell font-medium text-gray-900">{{ item.plateNumber }}</td>
              <td class="table-cell">{{ item.violationType }}</td>
              <td class="table-cell">{{ item.location }}</td>
              <td class="table-cell">
                <span class="badge" :class="statusClass(item.status)">{{ item.status }}</span>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive, nextTick } from 'vue';
import Chart from 'chart.js/auto';
import apiClient from '@/services/api';

const stats = reactive({
  totalToday: 0,
  processedToday: 0,
  pendingToday: 0,
  seriousToday: 0,
  totalChange: 0,
  processedChange: 0,
  pendingChange: 0,
  seriousChange: 0,
});
const realtimeWarnings = ref([]);
const recentViolations = ref([]);
const timeRange = ref('month');
const violationTypeChart = ref(null);
let chartInstance = null;

const violationColors = {
  '压实线': { bg: 'rgba(255, 159, 64, 0.8)', border: 'rgba(255, 159, 64, 1)' },
  '逆行': { bg: 'rgba(255, 99, 132, 0.8)', border: 'rgba(255, 99, 132, 1)' },
  '超速': { bg: 'rgba(239, 68, 68, 0.8)', border: 'rgba(239, 68, 68, 1)' },
  '违规停车': { bg: 'rgba(251, 191, 36, 0.8)', border: 'rgba(251, 191, 36, 1)' },
  '闯红灯': { bg: 'rgba(220, 38, 38, 0.8)', border: 'rgba(220, 38, 38, 1)' },
  'default': { bg: 'rgba(54, 162, 235, 0.8)', border: 'rgba(54, 162, 235, 1)' }
};

const fetchDashboardData = async (range = 'month') => {
  try {
    const response = await apiClient.get('/dashboard/data', { params: { timeRange: range } });
    const data = response.data;

    Object.assign(stats, data.stats);

    if (realtimeWarnings.value.length === 0) {
      realtimeWarnings.value = data.realtimeWarnings;
    }
    if (recentViolations.value.length === 0) {
      recentViolations.value = data.recentViolations;
    }

    await nextTick();
    updateChart(data.violationTypeDistribution);
  } catch (error) {
    console.error("加载仪表盘数据失败:", error);
  }
};

const fetchChartData = (newRange) => {
  timeRange.value = newRange;
  apiClient.get('/dashboard/data', { params: { timeRange: newRange } }).then(response => {
    updateChart(response.data.violationTypeDistribution);
  }).catch(error => {
    console.error("加载图表数据失败:", error);
  });
}

const updateChart = (chartData) => {
  if (chartInstance) {
    chartInstance.destroy();
  }
  if (violationTypeChart.value && chartData) {
    const backgroundColors = chartData.labels.map(label => violationColors[label]?.bg || violationColors.default.bg);
    const borderColors = chartData.labels.map(label => violationColors[label]?.border || violationColors.default.border);

    const ctx = violationTypeChart.value.getContext('2d');
    chartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: chartData.labels,
        datasets: [{
          label: '违法数量',
          data: chartData.data,
          backgroundColor: backgroundColors,
          borderColor: borderColors,
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: { y: { beginAtZero: true } }
      }
    });
  }
};

onMounted(() => {
  fetchDashboardData(timeRange.value);
});

const formatTime = (isoString) => {
  if (!isoString) return 'N/A';
  return new Date(isoString).toLocaleString('zh-CN', { hour12: false, year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }).replace(/\//g, '-');
};

const statusClass = (status) => {
  const map = {
    '待处理': 'bg-yellow-100 text-yellow-800',
    '已处理': 'bg-green-100 text-green-800',
    '处理中': 'bg-blue-100 text-blue-800',
    '已归档': 'bg-gray-100 text-gray-800'
  };
  return map[status] || 'bg-gray-200';
};
</script>

<style scoped>
.table-header {
  @apply px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider;
}
.table-cell {
  @apply px-6 py-4 whitespace-nowrap text-sm text-gray-500;
}
</style>
