<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">实时监控</h2>
      <p class="text-gray-600">查看克拉玛依市各路口实时监控和违法行为检测</p>
    </div>

    <div class="card mb-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
        <div class="flex flex-col sm:flex-row gap-3">
          <select v-model="selectedDistrict" class="input w-full sm:w-60">
            <option value="">全部区域</option>
            <option v-for="district in districts" :key="district.districtId" :value="district.districtId">
              {{ district.districtName }}
            </option>
          </select>
          <select v-model="selectedDevice" class="input w-full sm:w-60">
            <option value="">全部摄像头</option>
            <option v-for="camera in cameras" :key="camera.deviceId" :value="camera.deviceCode">
              {{ camera.deviceName }} ({{ camera.deviceCode }})
            </option>
          </select>
        </div>
        <div class="flex gap-2">
          <button @click="fetchCameras" class="btn btn-secondary">
            <i class="fa fa-refresh mr-1"></i> 刷新
          </button>
        </div>
      </div>

      <div v-if="isLoading" class="text-center py-10">加载中...</div>
      <div v-else-if="filteredCameras.length === 0" class="text-center py-10">无匹配的监控设备</div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <router-link
          v-for="camera in filteredCameras"
          :key="camera.deviceId"
          :to="{ name: 'monitoring-detail', params: { id: camera.deviceId } }"
          class="card bg-gray-900 p-0 overflow-hidden transition-all duration-300 hover:shadow-2xl hover:ring-2 hover:ring-primary"
        >
          <div class="relative">
            <img :src="camera.imageUrl" :alt="camera.deviceName" class="w-full h-48 object-cover">
            <div :class="['absolute top-2 left-2 text-white text-xs px-2 py-1 rounded', camera.status.toLowerCase() === 'online' ? 'bg-danger/80' : 'bg-gray-500/80']">
              <i class="fa mr-1" :class="camera.status.toLowerCase() === 'online' ? 'fa-circle animate-pulse' : 'fa-power-off'"></i>
              {{ camera.status.toLowerCase() === 'online' ? '直播中' : '离线' }}
            </div>
            <div class="absolute top-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              {{ camera.deviceCode }}
            </div>
            <div class="absolute bottom-2 left-2 bg-dark/80 text-white text-xs px-2 py-1 rounded max-w-[60%] truncate" :title="camera.address">
              {{ camera.address }}
            </div>
            <div class="absolute bottom-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              <i class="fa fa-exclamation-triangle text-warning mr-1"></i> {{ camera.violationCount }}起违法
            </div>
          </div>
          <div class="p-4">
            <h4 class="font-medium text-white">{{ camera.deviceName }}</h4>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onActivated } from 'vue';
import apiClient from '@/services/api';

const cameras = ref([]);
const districts = ref([]);
const isLoading = ref(true);

const selectedDistrict = ref('');
const selectedDevice = ref('');

// 修改点 2: 更新筛选逻辑
const filteredCameras = computed(() => {
  return cameras.value.filter(camera => {
    // 使用精确的 ID 匹配，而不是模糊的地址匹配
    const matchDistrict = !selectedDistrict.value || camera.districtId === selectedDistrict.value;
    const matchDevice = !selectedDevice.value || camera.deviceCode === selectedDevice.value;
    return matchDistrict && matchDevice;
  });
});

const fetchCameras = async () => {
  isLoading.value = true;
  try {
    const response = await apiClient.get('/devices/cameras/active');
    cameras.value = response.data;
  } catch (error) {
    console.error("加载摄像头列表失败:", error);
  } finally {
    isLoading.value = false;
  }
};

const fetchDistricts = async () => {
  try {
    const response = await apiClient.get('/districts');
    districts.value = response.data;
  } catch (error) {
    console.error("加载辖区列表失败:", error);
  }
};

const loadData = () => {
  fetchCameras();
  fetchDistricts();
}

onMounted(loadData);
onActivated(loadData);
</script>
