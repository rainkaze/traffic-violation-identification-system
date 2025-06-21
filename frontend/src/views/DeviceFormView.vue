<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">{{ pageTitle }}</h2>
      <p class="text-gray-600">{{ pageDescription }}</p>
    </div>

    <div class="max-w-4xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 card">
        <form @submit.prevent="submitForm">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">设备编码*</label>
              <input v-model="form.deviceCode" type="text" class="input mt-1 w-full" :disabled="isEditMode" required>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">设备名称*</label>
              <input v-model="form.deviceName" type="text" class="input mt-1 w-full" required>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">设备类型*</label>
              <select v-model="form.deviceType" class="input mt-1 w-full" required>
                <option>高清摄像头</option>
                <option>雷达测速仪</option>
                <option>AI识别终端</option>
                <option>GPU推理服务器</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">所属辖区*</label>
              <select v-model="form.districtId" class="input mt-1 w-full" required>
                <option v-for="district in districts" :key="district.districtId" :value="district.districtId">
                  {{ district.districtName }}
                </option>
              </select>
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-700">设备地址*</label>
              <input v-model="form.address" type="text" class="input mt-1 w-full" required>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">IP地址</label>
              <input v-model="form.ipAddress" type="text" class="input mt-1 w-full" placeholder="例如: 192.168.1.101">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">设备型号</label>
              <input v-model="form.modelName" type="text" class="input mt-1 w-full">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">设备状态*</label>
              <select v-model="form.status" class="input mt-1 w-full" required>
                <option value="online">在线</option>
                <option value="offline">离线</option>
                <option value="warning">警告</option>
                <option value="maintenance">维护中</option>
              </select>
            </div>
          </div>
          <div v-if="error" class="text-red-600 text-sm mt-4">{{ error }}</div>
          <div class="mt-6 flex justify-end gap-3">
            <router-link to="/devices" class="btn btn-secondary">返回列表</router-link>
            <button type="submit" :disabled="isLoading" class="btn btn-primary">
              {{ isLoading ? '保存中...' : '确认保存' }}
            </button>
          </div>
        </form>
      </div>

      <div class="card">
        <h3 class="font-bold text-gray-800 mb-4">连接与视频流测试</h3>
        <div class="aspect-video bg-black rounded-lg flex items-center justify-center text-gray-500 mb-4">
          <video v-if="showStream" class="w-full h-full" controls autoplay src="https://www.w3schools.com/html/mov_bbb.mp4"></video>
          <span v-else>视频流区域</span>
        </div>
        <button @click="testStream" class="btn btn-success w-full" :disabled="isTesting">
          <i class="fa fa-rss mr-2"></i>
          {{ isTesting ? '测试中...' : '开始测试连接' }}
        </button>
        <p v-if="testResult" class="text-sm text-center mt-2" :class="isSuccess ? 'text-success' : 'text-danger'">
          {{ testResult }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import apiClient from '@/services/api';

const props = defineProps({
  id: String,
});

const route = useRoute();
const router = useRouter();

const form = ref({});
const districts = ref([]);
const error = ref('');
const isLoading = ref(false);

const showStream = ref(false);
const isTesting = ref(false);
const testResult = ref('');
const isSuccess = ref(false);

const isEditMode = computed(() => !!props.id);
const pageTitle = computed(() => isEditMode.value ? '编辑设备' : '新建设备');
const pageDescription = computed(() => isEditMode.value ? '修改设备信息并测试连接。' : '填写新设备的信息并测试连接。');

const fetchDistricts = async () => {
  try {
    const response = await apiClient.get('/districts');
    districts.value = response.data;
  } catch (err) {
    console.error("加载辖区失败:", err);
  }
};

const loadDeviceData = async () => {
  if (!isEditMode.value) {
    form.value = { deviceType: '高清摄像头', status: 'online' };
    return;
  }
  try {
    const response = await apiClient.get(`/devices/${props.id}`);
    form.value = response.data;
  } catch (err) {
    alert('加载设备信息失败');
    router.push('/devices');
  }
};

const submitForm = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    const payload = form.value;
    if (isEditMode.value) {
      await apiClient.put(`/devices/${props.id}`, payload);
    } else {
      await apiClient.post('/devices', payload);
    }
    alert('保存成功！');
    router.push('/devices');
  } catch (err) {
    error.value = err.response?.data?.message || '操作失败';
  } finally {
    isLoading.value = false;
  }
};

const testStream = () => {
  isTesting.value = true;
  showStream.value = false;
  testResult.value = '正在尝试连接...';
  isSuccess.value = false;

  setTimeout(() => {
    // 模拟成功或失败
    if (Math.random() > 0.3) { // 70% 概率成功
      testResult.value = '连接成功，视频流已加载！';
      isSuccess.value = true;
      showStream.value = true;
    } else {
      testResult.value = '连接失败，请检查IP地址或网络。';
      isSuccess.value = false;
    }
    isTesting.value = false;
  }, 2000);
};

onMounted(() => {
  fetchDistricts();
  loadDeviceData();
});
</script>
