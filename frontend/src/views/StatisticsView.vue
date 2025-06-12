<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">统计分析</h2>
      <p class="text-gray-600">克拉玛依市交通违法数据深度洞察与趋势分析</p>
    </div>

    <div v-if="isLoading" class="text-center py-10">
      <p class="text-gray-500">正在加载统计数据...</p>
    </div>
    <div v-if="error" class="card bg-red-50 text-red-700 p-4 text-center">
      <p>{{ error }}</p>
    </div>

    <div v-if="!isLoading && statisticsData" class="space-y-6">
      <div class="card">
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div class="flex flex-col sm:flex-row gap-3 flex-wrap">
            <select class="input w-full sm:w-40">
              <option value="week" selected>按周</option>
              <option value="month">按月</option>
              <option value="year">按年</option>
            </select>
            <div class="relative">
              <input type="date" value="2025-06-09" class="input w-full sm:w-48">
            </div>
            <select class="input w-full sm:w-40">
              <option value="">全部区域</option>
              <option value="kmq">克拉玛依区</option>
              <option value="dsz">独山子区</option>
              <option value="bjt">白碱滩区</option>
            </select>
          </div>
          <div class="flex gap-2">
            <button class="btn btn-primary">
              <i class="fa fa-download mr-1"></i> 导出报告
            </button>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card h-80"><canvas ref="violationTrendChart"></canvas></div>
        <div class="card h-80"><canvas ref="peakTimeAnalysisChart"></canvas></div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">违法类型分布</h3>
          <div class="h-64"><canvas ref="violationTypePieChart"></canvas></div>
        </div>
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">区域违法分布</h3>
          <div class="h-64"><canvas ref="regionDistributionChart"></canvas></div>
        </div>
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">高发时段热力图</h3>
          <div class="h-64 flex items-center justify-center bg-gray-100 rounded-lg overflow-hidden">
            <img src="/reli.jpg" class="object-cover w-full h-full" alt="Heatmap placeholder"/>
          </div>
        </div>
      </div>

      <div class="card">
        <h3 class="font-bold text-gray-800 mb-4">违法高发地点 TOP 5</h3>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">排名</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">地点</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">区域</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">周违法次数</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">主要违法类型</th>
              <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">趋势</th>
            </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="item in statisticsData.topLocations" :key="item.rank" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">{{ item.rank }}</td>
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ item.location }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ item.region }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ item.count }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ item.primaryViolationType }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm">
                <span v-if="item.trend > 0" class="text-danger">
                  <i class="fa fa-arrow-up mr-1"></i> {{ (item.trend * 100).toFixed(0) }}%
                </span>
                <span v-else-if="item.trend < 0" class="text-success">
                  <i class="fa fa-arrow-down mr-1"></i> {{ (Math.abs(item.trend) * 100).toFixed(0) }}%
                </span>
                <span v-else class="text-gray-500">
                  -
                </span>
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
import { onMounted, ref } from 'vue';
import Chart from 'chart.js/auto';
import axios from 'axios';

// Refs for chart canvases
const violationTrendChart = ref(null);
const peakTimeAnalysisChart = ref(null);
const violationTypePieChart = ref(null);
const regionDistributionChart = ref(null);

// Refs for state management
const statisticsData = ref(null);
const isLoading = ref(true);
const error = ref(null);

// Fetch data from backend
const fetchStatistics = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await axios.get('http://localhost:8080/api/statistics');
    statisticsData.value = response.data;
    // Data is fetched, now we can initialize charts
    initCharts();
  } catch (err) {
    error.value = '无法加载统计数据。请确保后端服务正在运行且CORS配置正确。';
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

// Call fetchStatistics when the component is mounted
onMounted(fetchStatistics);

// Chart initialization function
const initCharts = () => {
  // Ensure we have data and the canvas elements are ready
  if (!statisticsData.value || !violationTrendChart.value) {
    return;
  }
  const data = statisticsData.value;

  new Chart(violationTrendChart.value.getContext('2d'), {
    type: 'line',
    data: {
      labels: data.violationTrend.labels,
      datasets: [{
        label: '本周违法',
        data: data.violationTrend.data,
        tension: 0.4,
        backgroundColor: 'rgba(30, 64, 175, 0.1)',
        borderColor: 'rgba(30, 64, 175, 1)',
        fill: true
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });

  new Chart(peakTimeAnalysisChart.value.getContext('2d'), {
    type: 'bar',
    data: {
      labels: data.peakTimeAnalysis.labels,
      datasets: [{
        label: '高峰时段违法',
        data: data.peakTimeAnalysis.data,
        backgroundColor: 'rgba(14, 165, 233, 0.8)'
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });

  new Chart(violationTypePieChart.value.getContext('2d'), {
    type: 'pie',
    data: {
      labels: data.violationTypeDistribution.labels,
      datasets: [{
        data: data.violationTypeDistribution.data,
        backgroundColor: ['#ef4444', '#f59e0b', '#10b981', '#1e40af']
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });

  new Chart(regionDistributionChart.value.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: data.regionDistribution.labels,
      datasets: [{
        data: data.regionDistribution.data,
        backgroundColor: ['#1e40af', '#0ea5e9', '#10b981', '#f59e0b']
      }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });
};
</script>
