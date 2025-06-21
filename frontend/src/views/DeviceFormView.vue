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

      <div class="card space-y-4">
        <div>
          <h3 class="font-bold text-gray-800 mb-2">设备绑定信息</h3>
          <div v-if="!createdDevice.id" class="text-sm text-gray-500 p-4 bg-gray-100 rounded-lg text-center">
            请先保存设备以生成绑定码
          </div>
          <div v-else class="p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <p class="text-sm text-gray-600">请在Android设备上输入以下绑定码：</p>
            <p class="text-2xl font-bold text-primary text-center my-2 tracking-widest bg-white py-1 rounded">{{ createdDevice.bindingCode }}</p>
            <p class="text-xs text-gray-500">此绑定码在 {{ formatTime(createdDevice.bindingCodeExpiresAt) }} 前有效。</p>
          </div>
        </div>

        <div>
          <h3 class="font-bold text-gray-800 mb-2">连接与视频流测试</h3>
          <div class="aspect-video bg-black rounded-lg flex items-center justify-center text-gray-500 mb-4">
            <video ref="videoPlayer" class="w-full h-full" autoplay playsinline muted></video>
          </div>
          <button @click="startViewer(createdDevice.id)" class="btn btn-success w-full" :disabled="!createdDevice.id || isConnecting || isConnected">
            <i class="fa fa-rss mr-2"></i>
            {{ isConnecting ? '连接中...' : (isConnected ? '正在播放' : '开始测试连接') }}
          </button>
          <p class="text-sm text-center mt-2" :class="statusColor">
            状态: {{ connectionStatus }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import apiClient from '@/services/api';
import { useWebRTC } from '@/composables/useWebRTC';

const props = defineProps({ id: String });
const route = useRoute();
const router = useRouter();

const form = ref({});
const createdDevice = reactive({
  id: null,
  bindingCode: null,
  bindingCodeExpiresAt: null,
});
const districts = ref([]);
const error = ref('');
const isLoading = ref(false);

// 使用 WebRTC Composable
const { videoPlayer, connectionStatus, statusColor, isConnected, isConnecting, startViewer } = useWebRTC();

const isEditMode = computed(() => !!props.id);
const pageTitle = computed(() => isEditMode.value ? '编辑设备' : '新建设备');
const pageDescription = computed(() => isEditMode.value ? '修改设备信息并测试连接。' : '填写新设备的信息，获取绑定码并测试连接。');

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
    form.value = { deviceType: '高清摄像头' }; // 移除status默认值
    return;
  }
  try {
    const response = await apiClient.get(`/devices/${props.id}`);
    form.value = response.data;
    // 如果是编辑模式，也填充createdDevice信息
    Object.assign(createdDevice, {
      id: response.data.deviceId,
      bindingCode: response.data.bindingCode,
      bindingCodeExpiresAt: response.data.bindingCodeExpiresAt,
    });
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
      alert('设备更新成功！');
      router.push('/devices');
    } else {
      const response = await apiClient.post('/devices', payload);
      // [核心修改] 创建成功后，不跳转，而是更新UI
      Object.assign(createdDevice, {
        id: response.data.deviceId,
        bindingCode: response.data.bindingCode,
        bindingCodeExpiresAt: response.data.bindingCodeExpiresAt,
      });
      alert('设备创建成功！请使用绑定码在手机端激活。');
    }
  } catch (err) {
    error.value = err.response?.data?.message || '操作失败';
  } finally {
    isLoading.value = false;
  }
};

const formatTime = (isoString) => {
  if (!isoString) return 'N/A';
  return new Date(isoString).toLocaleString('zh-CN', { hour12: false });
};

onMounted(() => {
  fetchDistricts();
  loadDeviceData();
});
</script>
