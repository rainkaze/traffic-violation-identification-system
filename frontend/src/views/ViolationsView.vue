<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">违法记录管理</h2>
      <p class="text-gray-600">{{ pageDescription }}</p>
    </div>

    <div class="card mb-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
        <div class="flex flex-col sm:flex-row gap-3 flex-wrap">
          <div class="relative">
            <input type="text" v-model="filters.plateNumber" @input="onFilterChange" placeholder="    搜索车牌号" class="input pl-10 w-full sm:w-48">
            <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
          </div>
          <select v-model="filters.violationType" @change="onFilterChange" class="input w-full sm:w-48">
            <option value="">全部违法类型</option>
            <option v-for="type in violationTypes" :key="type" :value="type">
              {{ type }}
            </option>
          </select>
          <select v-model="filters.districtId" @change="onFilterChange" class="input w-full sm:w-40">
            <option value="">全部辖区</option>
            <option v-for="district in availableDistricts" :key="district.districtId" :value="district.districtId">
              {{ district.districtName }}
            </option>
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
          <div class="relative">
            <button @click="showExportOptions = !showExportOptions" class="btn btn-primary"><i class="fa fa-download mr-1"></i> 导出数据</button>
            <div v-if="showExportOptions" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-20">
              <a href="#" @click.prevent="handleExport('pdf')" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">导出为 PDF</a>
              <a href="#" @click.prevent="handleExport('xlsx')" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">导出为 Excel (XLSX)</a>
              <a href="#" @click.prevent="handleExport('csv')" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">导出为 CSV</a>
            </div>
          </div>
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
              <span :class="{'bg-yellow-100 text-yellow-800': item.status === '待处理', 'bg-green-100 text-green-800': item.status === '已处理', 'bg-blue-100 text-blue-800': item.status === '处理中', 'bg-gray-100 text-gray-800': item.status === '已归档'}" class="badge">{{ item.status }}</span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button @click="handleProcessClick(item.id)" class="text-primary hover:text-primary/80 mr-3">详情</button>
              <button @click="handleProcessClick(item.id)" class="text-success hover:text-success/80">处理</button>
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
import { ref, onMounted, reactive, computed } from 'vue';
import apiClient from '@/services/api';
import authStore from '@/store/auth'; // 引入权限状态
import { debounce } from 'lodash';
import { useRouter } from 'vue-router'; // 引入 useRouter

const router = useRouter(); // 获取 router 实例
const violations = ref([]);
const error = ref(null);
const loading = ref(true);
const violationTypes = ref([]);
const allDistricts = ref([]); // 存储所有辖区列表
const showExportOptions = ref(false);

// 从 authStore 中获取用户信息和角色
const isAdmin = computed(() => authStore.isAdmin());
const currentUser = computed(() => authStore.currentUser());

// 动态生成页面描述
const pageDescription = computed(() => {
  return isAdmin.value
    ? '查询、筛选和管理克拉玛依市所有交通违法记录'
    : '查询、筛选您所管辖区的交通违法记录';
});
const handleProcessClick = async (violationId) => {
  try {
    // 先调用后端接口，判断是否匹配工作流
    const response = await apiClient.post(`/processing/initiate/${violationId}`);
    const { isWorkflowCase } = response.data;

    // 根据后端返回结果，导航到处理页面
    router.push({ name: 'process-violation', params: { id: violationId } });

  } catch (error) {
    alert('启动处理流程失败: ' + (error.response?.data?.message || '未知错误'));
  }
};
// 根据用户角色，计算出下拉框中可用的辖区
const availableDistricts = computed(() => {
  if (isAdmin.value) {
    return allDistricts.value;
  }
  if (currentUser.value && currentUser.value.districts) {
    return allDistricts.value.filter(d => currentUser.value.districts.includes(d.districtName));
  }
  return [];
});

const filters = reactive({
  plateNumber: '',
  violationType: '',
  status: '',
  yearMonth: '',
  districtId: '', // 新增辖区筛选字段
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  totalPages: 1,
  totalItems: 0
});

const fetchViolationTypes = async () => {
  try {
    const response = await apiClient.get('/rules/types');
    violationTypes.value = response.data;
  } catch (err) {
    console.error("加载违法类型失败:", err);
  }
};

const fetchAllDistricts = async () => {
  try {
    const response = await apiClient.get('/districts');
    allDistricts.value = response.data;
  } catch (error) {
    console.error("加载所有辖区失败:", error);
  }
};

const fetchViolations = async () => {
  loading.value = true;
  error.value = null;
  try {
    const params = {
      page: pagination.currentPage,
      pageSize: pagination.pageSize,
      ...filters
    };

    const response = await apiClient.get('/violations', { params });
    const data = response.data;

    violations.value = data.items;
    pagination.totalItems = data.totalItems;
    pagination.totalPages = data.totalPages;
    pagination.currentPage = data.currentPage;
  } catch (err) {
    error.value = '加载违法记录失败。请检查网络连接或联系管理员。';
    console.error("Error fetching violations:", err.response || err);
  } finally {
    loading.value = false;
  }
};

const debouncedFetch = debounce(() => {
  pagination.currentPage = 1;
  fetchViolations();
}, 300);

const onFilterChange = () => {
  debouncedFetch();
};

const changePage = (page) => {
  if (page > 0 && page <= pagination.totalPages) {
    pagination.currentPage = page;
    fetchViolations();
  }
};

const formatTime = (isoString) => {
  if (!isoString) return 'N/A';
  return new Date(isoString).toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-');
};
const handleExport = async (format) => {
  showExportOptions.value = false; // Hide dropdown
  try {
    const params = { ...filters, format };
    const response = await apiClient.get('/violations/export', {
      params,
      responseType: 'blob', // Important
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `violations.${format}`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (err) {
    console.error("导出失败:", err);
    alert('导出失败，请查看控制台获取更多信息。');
  }
};
onMounted(async () => {
  // 先获取辖区数据，以便筛选框能正常显示
  await fetchAllDistricts();
  // 然后获取违法记录和类型
  fetchViolations();
  fetchViolationTypes();
});
</script>
