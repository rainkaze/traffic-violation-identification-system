<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">三方连接测试页面</h2>
      <p class="text-gray-600">此页面用于验证AI模型端 -> 后端 -> 前端的数据链路是否通畅。</p>
    </div>

    <div class="card">
      <div class="flex justify-between items-center mb-4">
        <h3 class="font-bold text-lg">从AI端接收到的最新违法记录</h3>
        <button @click="fetchLatestViolations" class="btn btn-secondary" :disabled="isLoading">
          <i class="fa fa-refresh mr-1" :class="{'animate-spin': isLoading}"></i>
          {{ isLoading ? '刷新中...' : '手动刷新' }}
        </button>
      </div>

      <div v-if="isLoading && violations.length === 0" class="text-center py-10">
        正在加载数据...
      </div>
      <div v-else-if="error" class="text-center py-10 text-red-600">
        {{ error }}
      </div>
      <div v-else-if="violations.length === 0" class="text-center py-10 text-gray-500">
        暂无记录。请确保Python AI脚本正在运行。
      </div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left">车牌号</th>
            <th class="px-6 py-3 text-left">违法时间</th>
            <th class="px-6 py-3 text-left">设备</th>
            <th class="px-6 py-3 text-left">状态</th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="item in violations" :key="item.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 font-medium">{{ item.plate }}</td>
            <td class="px-6 py-4">{{ formatTime(item.time) }}</td>
            <td class="px-6 py-4">{{ item.device }}</td>
            <td class="px-6 py-4">
              <span class="badge bg-yellow-100 text-yellow-800">{{ item.status }}</span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import apiClient from '@/services/api';

const violations = ref([]);
const isLoading = ref(false);
const error = ref(null);
let pollInterval;

const fetchLatestViolations = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const response = await apiClient.get('/test/latest-violations');
    violations.value = response.data;
  } catch (err) {
    error.value = '获取最新记录失败。请检查后端服务是否正常。';
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const formatTime = (isoString) => {
  if (!isoString) return 'N/A';
  return new Date(isoString).toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-');
};

onMounted(() => {
  fetchLatestViolations();
  // 每5秒自动刷新一次
  pollInterval = setInterval(fetchLatestViolations, 5000);
});

onUnmounted(() => {
  // 离开页面时停止轮询
  clearInterval(pollInterval);
});
</script>
