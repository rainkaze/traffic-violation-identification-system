<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">违法记录管理</h2>
      <p class="text-gray-600">查询、筛选和管理克拉玛依市所有交通违法记录</p>
    </div>

    <div class="card mb-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
        <div class="flex flex-col sm:flex-row gap-3 flex-wrap">
          <div class="relative">
            <input type="text" placeholder="搜索车牌号" class="input pl-10 w-full sm:w-48">
            <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
          <select class="input w-full sm:w-40">
            <option value="">全部违法类型</option>
            <option value="1">闯红灯</option>
            <option value="2">超速行驶</option>
            <option value="3">逆行</option>
            <option value="4">不按导向车道行驶</option>
            <option value="5">违法变道</option>
          </select>
          <input type="month" value="2025-06" class="input w-full sm:w-40">
          <select class="input w-full sm:w-32">
            <option value="">全部状态</option>
            <option value="pending">待处理</option>
            <option value="processed">已处理</option>
          </select>
        </div>
        <div class="flex gap-2">
          <button class="btn btn-secondary">
            <i class="fa fa-upload mr-1"></i> 批量导入
          </button>
          <button class="btn btn-primary">
            <i class="fa fa-download mr-1"></i> 导出数据
          </button>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              <input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary">
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">时间</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">车牌号码</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">违法类型</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">地点</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">设备</th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
            <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="error" class="text-center">
            <td colspan="8" class="py-4 text-red-500">{{ error }}</td>
          </tr>
          <tr v-for="item in violations" :key="item.id" class="hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 whitespace-nowrap">
              <input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary">
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.time }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ item.plate }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.type }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.location }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.device }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="item.status === '待处理' ? 'bg-warning' : 'bg-success'" class="badge text-white">{{ item.status }}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button class="text-primary hover:text-primary/80 mr-3">详情</button>
              <button class="text-success hover:text-success/80">处理</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between mt-4 pt-4 border-t border-gray-200">
        <div class="text-sm text-gray-700">
          显示 <span class="font-medium">1</span> 到 <span class="font-medium">5</span> 条，共 <span class="font-medium">158</span> 条记录
        </div>
        <div class="flex space-x-1">
          <button class="btn btn-secondary px-3 py-1 text-sm">上一页</button>
          <button class="btn btn-primary px-3 py-1 text-sm">1</button>
          <button class="btn btn-secondary px-3 py-1 text-sm">2</button>
          <button class="btn btn-secondary px-3 py-1 text-sm">3</button>
          <button class="btn btn-secondary px-3 py-1 text-sm">...</button>
          <button class="btn btn-secondary px-3 py-1 text-sm">32</button>
          <button class="btn btn-secondary px-3 py-1 text-sm">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const violations = ref([]);
const error = ref(null);

const fetchViolations = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/violations');
    violations.value = response.data;
  } catch (err) {
    error.value = '无法从后端加载违法记录。请确保后端服务正在运行且CORS配置正确。';
    console.error(err);
  }
};

onMounted(fetchViolations);
</script>
