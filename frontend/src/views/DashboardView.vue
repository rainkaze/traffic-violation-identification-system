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
            <h3 class="text-3xl font-bold mt-1">158</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa fa-arrow-up text-green-300 mr-1"></i>
              <span>7% 较昨日</span>
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
            <h3 class="text-3xl font-bold mt-1">121</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa fa-arrow-up text-green-300 mr-1"></i>
              <span>5% 较昨日</span>
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
            <h3 class="text-3xl font-bold mt-1">37</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa fa-arrow-up text-red-300 mr-1"></i>
              <span>11% 较昨日</span>
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
            <h3 class="text-3xl font-bold mt-1">18</h3>
            <p class="text-xs mt-2 flex items-center">
              <i class="fa fa-arrow-down text-green-300 mr-1"></i>
              <span>-3% 较昨日</span>
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
          <h3 class="font-bold text-gray-800">违法类型分布 (本月)</h3>
          <div class="flex space-x-2">
            <button class="btn btn-secondary text-sm">今日</button>
            <button class="btn btn-secondary text-sm">本周</button>
            <button class="btn btn-primary text-sm">本月</button>
          </div>
        </div>
        <div class="h-80"><canvas ref="violationTypeChart"></canvas></div>
      </div>

      <div class="card">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">实时预警</h3>
          <button class="text-primary hover:text-primary/80 text-sm">查看全部</button>
        </div>
        <div class="h-80 overflow-y-auto space-y-3">
          <div class="flex items-center gap-3 p-2 rounded-lg bg-red-50 border-l-4 border-danger">
            <i class="fa fa-car text-danger text-lg"></i>
            <div>
              <p class="font-medium text-sm">新K·A12345 闯红灯</p>
              <p class="text-xs text-gray-500">1分钟前 @ 世纪大道</p>
            </div>
            <span class="ml-auto text-xs font-bold text-danger">一级预警</span>
          </div>
          <div class="flex items-center gap-3 p-2 rounded-lg bg-red-50 border-l-4 border-danger">
            <i class="fa fa-car text-danger text-lg"></i>
            <div>
              <p class="font-medium text-sm">新K·B67890 逆行</p>
              <p class="text-xs text-gray-500">3分钟前 @ 友谊路</p>
            </div>
            <span class="ml-auto text-xs font-bold text-danger">一级预警</span>
          </div>
          <div class="flex items-center gap-3 p-2 rounded-lg bg-yellow-50 border-l-4 border-warning">
            <i class="fa fa-tachometer text-warning text-lg"></i>
            <div>
              <p class="font-medium text-sm">新K·C24681 超速</p>
              <p class="text-xs text-gray-500">5分钟前 @ 迎宾路快速道</p>
            </div>
            <span class="ml-auto text-xs font-bold text-warning">二级预警</span>
          </div>
          <div class="flex items-center gap-3 p-2 rounded-lg bg-yellow-50 border-l-4 border-warning">
            <i class="fa fa-tachometer text-warning text-lg"></i>
            <div>
              <p class="font-medium text-sm">新K·D35792 超速</p>
              <p class="text-xs text-gray-500">8分钟前 @ 克南高架</p>
            </div>
            <span class="ml-auto text-xs font-bold text-warning">二级预警</span>
          </div>
          <div class="flex items-center gap-3 p-2 rounded-lg bg-red-50 border-l-4 border-danger">
            <i class="fa fa-exchange text-danger text-lg"></i>
            <div>
              <p class="font-medium text-sm">新K·E46803 违法变道</p>
              <p class="text-xs text-gray-500">12分钟前 @ 和平路</p>
            </div>
            <span class="ml-auto text-xs font-bold text-danger">一级预警</span>
          </div>
        </div>
      </div>

      <div class="card lg:col-span-3">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-gray-800">最近违法记录</h3>
          <router-link to="/violations" class="text-primary hover:text-primary/80 flex items-center">
            <span>查看全部</span>
            <i class="fa fa-angle-right ml-1"></i>
          </router-link>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">时间</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">车牌号码</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">违法类型</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">地点</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
              <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
            </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">2025-06-09 10:23:45</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">新K·A12345</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">闯红灯</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">克拉玛依区世纪大道与友谊路交叉口</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="badge bg-warning text-white">待处理</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button class="text-primary hover:text-primary/80 mr-3">详情</button>
                <button class="text-success hover:text-success/80">处理</button>
              </td>
            </tr>
            <tr class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">2025-06-09 09:47:12</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">新K·B67890</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">超速行驶</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">独山子区G30高速K3550+200</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="badge bg-success text-white">已处理</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button class="text-primary hover:text-primary/80 mr-3">详情</button>
                <button class="text-success hover:text-success/80">处理</button>
              </td>
            </tr>
            <tr class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">2025-06-09 08:15:30</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">新K·C24681</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">逆行</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">白碱滩区和平路</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="badge bg-warning text-white">待处理</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button class="text-primary hover:text-primary/80 mr-3">详情</button>
                <button class="text-success hover:text-success/80">处理</button>
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

const violationTypeChart = ref(null);
const isMounted = ref(false);

onMounted(() => {
  isMounted.value = true;
  nextTick(() => {
    // 初始化违法类型分布图表
    if (violationTypeChart.value) {
      const typeCtx = violationTypeChart.value.getContext('2d');
      new Chart(typeCtx, {
        type: 'bar',
        data: {
          labels: ['闯红灯', '超速行驶', '逆行', '不按导向车道行驶', '违法变道', '压线行驶'],
          datasets: [{
            label: '违法数量',
            data: [55, 42, 24, 38, 18, 20],
            backgroundColor: [
              'rgba(30, 64, 175, 0.8)',
              'rgba(14, 165, 233, 0.8)',
              'rgba(16, 185, 129, 0.8)',
              'rgba(245, 158, 11, 0.8)',
              'rgba(239, 68, 68, 0.8)',
              'rgba(139, 92, 246, 0.8)'
            ],
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
    }
  });
});
</script>
