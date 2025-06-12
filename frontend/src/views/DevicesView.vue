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
            <input type="text" placeholder="搜索设备ID或名称" class="input pl-10 w-full sm:w-64">
            <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
          <select class="input w-full sm:w-40">
            <option value="">全部类型</option>
            <option value="camera">高清摄像头</option>
            <option value="radar">雷达测速仪</option>
            <option value="ai">AI识别终端</option>
          </select>
          <select class="input w-full sm:w-40">
            <option value="">全部状态</option>
            <option value="online">在线</option>
            <option value="offline">离线</option>
            <option value="warning">警告</option>
          </select>
        </div>
        <div class="flex gap-2">
          <button class="btn btn-primary">
            <i class="fa fa-plus mr-1"></i> 添加设备
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div class="card border-l-4 border-primary">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="font-bold text-gray-800">KMQ-CAM-001</h3>
              <p class="text-sm text-gray-600">克拉玛依区世纪大道...</p>
            </div>
            <span class="badge bg-success text-white">在线</span>
          </div>
          <div class="mt-4 pt-3 border-t text-sm text-gray-600 space-y-1">
            <p>类型: <span class="font-medium text-gray-800">高清摄像头</span></p>
            <p>型号: <span class="font-medium text-gray-800">YOLOv5s-ONNX</span></p>
            <p>最后活动: <span class="font-medium text-gray-800">刚刚</span></p>
          </div>
        </div>

        <div class="card border-l-4 border-danger">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="font-bold text-gray-800">DSZ-CAM-004</h3>
              <p class="text-sm text-gray-600">独山子区石化大道...</p>
            </div>
            <span class="badge bg-danger text-white">离线</span>
          </div>
          <div class="mt-4 pt-3 border-t text-sm text-gray-600 space-y-1">
            <p>类型: <span class="font-medium text-gray-800">高清摄像头</span></p>
            <p>型号: <span class="font-medium text-gray-800">YOLOv5s-ONNX</span></p>
            <p>最后活动: <span class="font-medium text-gray-800">2小时前</span></p>
          </div>
        </div>

        <div class="card border-l-4 border-warning">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="font-bold text-gray-800">RTX3090-SERVER-01</h3>
              <p class="text-sm text-gray-600">市局数据中心</p>
            </div>
            <span class="badge bg-warning text-white">警告</span>
          </div>
          <div class="mt-4 pt-3 border-t text-sm text-gray-600 space-y-1">
            <p>类型: <span class="font-medium text-gray-800">GPU推理服务器</span></p>
            <p>状态: <span class="font-medium text-yellow-600">TensorRT推理延迟>500ms</span></p>
            <p>最后活动: <span class="font-medium text-gray-800">刚刚</span></p>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-gray-800">设备状态统计</h3>
        <div class="flex space-x-2">
          <button class="btn btn-secondary text-sm">今日</button>
          <button class="btn btn-secondary text-sm">本周</button>
          <button class="btn btn-primary text-sm">本月</button>
        </div>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="h-64"><canvas ref="deviceStatusChart"></canvas></div>
        <div class="h-64"><canvas ref="deviceTypeChart"></canvas></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, nextTick} from 'vue';
import Chart from 'chart.js/auto';

const deviceStatusChart = ref(null);
const deviceTypeChart = ref(null);

onMounted(() => {
  nextTick(() => {
    // 设备状态图表
    new Chart(deviceStatusChart.value.getContext('2d'), {
      type: 'doughnut',
      data: {
        labels: ['在线', '离线', '警告'],
        datasets: [{
          data: [82, 8, 5],
          backgroundColor: ['rgba(16, 185, 129, 0.8)', 'rgba(239, 68, 68, 0.8)', 'rgba(245, 158, 11, 0.8)']
        }]
      },
      options: {responsive: true, maintainAspectRatio: false}
    });

    // 设备类型图表
    new Chart(deviceTypeChart.value.getContext('2d'), {
      type: 'bar',
      data: {
        labels: ['高清摄像头', 'AI识别终端', '雷达测速仪', 'GPU服务器'],
        datasets: [{
          label: '设备数量',
          data: [65, 15, 10, 5],
          backgroundColor: 'rgba(30, 64, 175, 0.8)'
        }]
      },
      options: {responsive: true, maintainAspectRatio: false}
    });
  });
});
</script>
