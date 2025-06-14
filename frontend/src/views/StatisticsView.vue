<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">统计分析</h2>
      <p class="text-gray-600">克拉玛依市交通违法数据深度洞察与趋势分析</p>
    </div>

    <div v-if="isLoading" class="text-center py-10">
      <p class="text-gray-500">正在加载统计数据...</p>
    </div>
    <div v-else-if="error" class="card bg-red-50 text-red-700 p-4 text-center">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="statisticsData" class="space-y-6">
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
          <h3 class="font-bold text-gray-800 mb-4">高发时段热力图 (模拟)</h3>
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
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">总违法次数</th>
              <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">趋势 (模拟)</th>
            </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="item in statisticsData.topLocations" :key="item.rank" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap font-bold">{{ item.rank }}</td>
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ item.location }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ item.region }}</td>
              <td class="px-6 py-4 whitespace-nowrap font-bold text-lg">{{ item.count }}</td>
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
import { onMounted, ref, nextTick } from 'vue';
import Chart from 'chart.js/auto';
import apiClient from '@/services/api';

const violationTrendChart = ref(null);
const peakTimeAnalysisChart = ref(null);
const violationTypePieChart = ref(null);
const regionDistributionChart = ref(null);

const statisticsData = ref(null);
const isLoading = ref(true);
const error = ref(null);

let chartInstances = [];

const destroyCharts = () => {
  chartInstances.forEach(chart => chart.destroy());
  chartInstances = [];
};

const fetchStatistics = async () => {
  isLoading.value = true;
  error.value = null;
  destroyCharts(); // 在获取新数据前销毁旧图表实例
  try {
    const response = await apiClient.get('/statistics');
    statisticsData.value = response.data;
    await nextTick();
    initCharts();
  } catch (err) {
    error.value = '无法加载统计数据。请确保后端服务正常且数据库连接无误。';
    console.error("Error fetching statistics:", err.response || err);
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchStatistics);

const initCharts = () => {
  if (!statisticsData.value) return;
  const data = statisticsData.value;
  const chartColors = ['#1e40af', '#0ea5e9', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#d946ef'];

  if (violationTrendChart.value && data.violationTrend) {
    const chart = new Chart(violationTrendChart.value.getContext('2d'), {
      type: 'line',
      data: {
        labels: data.violationTrend.labels,
        datasets: [{
          label: '每日违法数量',
          data: data.violationTrend.data,
          tension: 0.1,
          backgroundColor: 'rgba(30, 64, 175, 0.1)',
          borderColor: 'rgba(30, 64, 175, 1)',
          fill: true
        }]
      },
      options: { responsive: true, maintainAspectRatio: false, plugins: { title: { display: true, text: '最近30天违法趋势' } } }
    });
    chartInstances.push(chart);
  }

  if (peakTimeAnalysisChart.value && data.peakTimeAnalysis) {
    const chart = new Chart(peakTimeAnalysisChart.value.getContext('2d'), {
      type: 'bar',
      data: {
        labels: data.peakTimeAnalysis.labels,
        datasets: [{
          label: '违法数量',
          data: data.peakTimeAnalysis.data,
          backgroundColor: 'rgba(14, 165, 233, 0.8)'
        }]
      },
      options: { responsive: true, maintainAspectRatio: false, plugins: { title: { display: true, text: '24小时违法分布' } } }
    });
    chartInstances.push(chart);
  }

  if (violationTypePieChart.value && data.violationTypeDistribution) {
    const chart = new Chart(violationTypePieChart.value.getContext('2d'), {
      type: 'pie',
      data: {
        labels: data.violationTypeDistribution.labels,
        datasets: [{
          data: data.violationTypeDistribution.data,
          backgroundColor: chartColors
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });
    chartInstances.push(chart);
  }

  if (regionDistributionChart.value && data.regionDistribution) {
    const chart = new Chart(regionDistributionChart.value.getContext('2d'), {
      type: 'doughnut',
      data: {
        labels: data.regionDistribution.labels,
        datasets: [{
          data: data.regionDistribution.data,
          backgroundColor: chartColors
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });
    chartInstances.push(chart);
  }
};
</script>
