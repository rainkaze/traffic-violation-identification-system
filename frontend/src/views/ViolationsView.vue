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
            <input type="text" v-model="filters.plateNumber" @input="onFilterChange" placeholder="搜索车牌号" class="input pl-10 w-full sm:w-48">
            <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
          <select v-model="filters.violationType" @change="onFilterChange" class="input w-full sm:w-40">
            <option value="">全部违法类型</option>
            <option value="闯红灯">闯红灯</option>
            <option value="超速行驶">超速行驶</option>
            <option value="逆行">逆行</option>
            <option value="不按导向车道行驶">不按导向车道行驶</option>
            <option value="违法变道">违法变道</option>
            <option value="违章停车">违章停车</option>
          </select>
          <input type="month" v-model="filters.yearMonth" @change="onFilterChange" class="input w-full sm:w-40">
          <select v-model="filters.status" @change="onFilterChange" class="input w-full sm:w-32">
            <option value="">全部状态</option>
            <option value="待处理">待处理</option>
            <option value="已处理">已处理</option>
            <option value="处理中">处理中</option>
            <option value="已归档">已归档</option>
          </select>
        </div>
        <div class="flex gap-2">
          <button class="btn btn-secondary"><i class="fa fa-upload mr-1"></i> 批量导入</button>
          <button class="btn btn-primary"><i class="fa fa-download mr-1"></i> 导出数据</button>
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
          <tr v-if="loading"><td colspan="8" class="text-center py-4 text-gray-500">正在加载数据...</td></tr>
          <tr v-else-if="error"><td colspan="8" class="text-center py-4 text-red-500">{{ error }}</td></tr>
          <tr v-else-if="violations.length === 0"><td colspan="8" class="text-center py-4 text-gray-500">未找到符合条件的记录</td></tr>
          <tr v-for="item in violations" :key="item.id" class="hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 whitespace-nowrap"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"></td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatTime(item.time) }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ item.plate }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.type }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.location }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ item.device }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span :class="{'bg-warning': item.status === '待处理', 'bg-success': item.status === '已处理', 'bg-blue-500': item.status === '处理中'}" class="badge text-white">{{ item.status }}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button class="text-primary hover:text-primary/80 mr-3">详情</button>
              <button class="text-success hover:text-success/80">处理</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between mt-4 pt-4 border-t border-gray-200" v-if="!loading && pagination.totalItems > 0">
        <div class="text-sm text-gray-700">
          显示第 <span class="font-medium">{{ (pagination.currentPage - 1) * pagination.pageSize + 1 }}</span>
          到 <span class="font-medium">{{ Math.min(pagination.currentPage * pagination.pageSize, pagination.totalItems) }}</span>
          条，共 <span class="font-medium">{{ pagination.totalItems }}</span> 条记录
        </div>
        <div class="flex space-x-1">
          <button @click="changePage(pagination.currentPage - 1)" :disabled="pagination.currentPage <= 1" class="btn btn-secondary px-3 py-1 text-sm disabled:opacity-50 disabled:cursor-not-allowed">上一页</button>
          <span class="px-4 py-1 text-sm flex items-center">第 {{ pagination.currentPage }} / {{ pagination.totalPages }} 页</span>
          <button @click="changePage(pagination.currentPage + 1)" :disabled="pagination.currentPage >= pagination.totalPages" class="btn btn-secondary px-3 py-1 text-sm disabled:opacity-50 disabled:cursor-not-allowed">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, reactive} from 'vue';
import axios from 'axios';
import {debounce} from 'lodash';

const violations = ref([]);
const error = ref(null);
const loading = ref(true); // 初始为true，表示正在加载

// 筛选条件响应式对象
const filters = reactive({
  plateNumber: '',
  violationType: '',
  status: '',
  yearMonth: '' // e.g. "2025-06"
});

// 分页信息响应式对象
const pagination = reactive({
  currentPage: 1,
  pageSize: 5, // 每页显示5条
  totalPages: 1,
  totalItems: 0
});

const fetchViolations = async () => {
  loading.value = true;
  error.value = null;
  try {
    // 准备请求参数，只包含有值的筛选条件
    const params = {
      page: pagination.currentPage,
      pageSize: pagination.pageSize
    };
    for (const key in filters) {
      if (filters[key]) {
        params[key] = filters[key];
      }
    }

    const response = await axios.get('http://localhost:8080/api/violations', {params});
    const data = response.data;

    violations.value = data.items;
    pagination.totalItems = data.totalItems;
    pagination.totalPages = data.totalPages;
    pagination.currentPage = data.currentPage;
  } catch (err) {
    error.value = '无法从后端加载违法记录。请确保后端服务正在运行且网络和CORS配置正确。';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

// 使用防抖函数包装fetchViolations，避免因用户快速输入/选择而发起过多请求
const debouncedFetch = debounce(() => {
  pagination.currentPage = 1; // 筛选条件变化时，重置到第一页
  fetchViolations();
}, 500); // 用户停止输入500毫秒后执行

// 当任何筛选条件变化时，调用防抖函数
const onFilterChange = () => {
  debouncedFetch();
};

// 改变页码
const changePage = (page) => {
  if (page > 0 && page <= pagination.totalPages) {
    pagination.currentPage = page;
    fetchViolations();
  }
};

// 格式化时间 (处理ISO字符串)
const formatTime = (isoString) => {
  if (!isoString || typeof isoString !== 'string') {
    return '';
  }
  const dt = new Date(isoString);
  if (isNaN(dt.getTime())) {
    return '';
  }
  return dt.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).replace(/\//g, '-');
};

// 组件挂载时首次加载数据
onMounted(fetchViolations);
</script>
