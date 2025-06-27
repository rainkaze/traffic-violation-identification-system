<template>
  <div class="p-4 sm:p-6 lg:p-8">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">{{ pageTitle }}</h2>
      <p class="text-gray-600">{{ pageDescription }}</p>
    </div>

    <div class="max-w-6xl mx-auto p-6 md:p-8 card space-y-8">
      <!-- 表单区域 -->
      <form @submit.prevent="submitForm" class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- 左侧表单 -->
        <div class="lg:col-span-2 space-y-6">
          <h3 class="text-xl font-semibold text-gray-800 border-b border-gray-200 pb-3 mb-4">
            <i class="fa fa-info-circle mr-2 text-primary"></i>设备基础信息
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700">设备名称*</label>
              <input v-model="form.deviceName" type="text" class="input mt-1 w-full" required placeholder="例如：世纪大道与友谊路口摄像机">
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
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">所属辖区*</label>
            <select v-model="form.districtId" class="input mt-1 w-full" required>
              <option :value="null" disabled>请选择一个辖区</option>
              <option v-for="district in districts" :key="district.districtId" :value="district.districtId">
                {{ district.districtName }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">设备物理地址*</label>
            <input v-model="form.address" type="text" class="input mt-1 w-full" required placeholder="详细到路口或门牌号">
          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700">经度 (Longitude)</label>
              <input v-model.number="form.longitude" type="number" step="any" class="input mt-1 w-full" placeholder="例如: 116.397128">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">纬度 (Latitude)</label>
              <input v-model.number="form.latitude" type="number" step="any" class="input mt-1 w-full" placeholder="例如: 39.916527">
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">RTSP 视频流地址</label>
            <input v-model="form.rtspUrl" type="text" class="input mt-1 w-full" placeholder="例如: rtsp://127.0.0.1:8554/live/cam1">
          </div>
          <!-- *** 关键新增：设备状态下拉框 *** -->
          <div>
            <label class="block text-sm font-medium text-gray-700">设备状态*</label>
            <select v-model="form.status" class="input mt-1 w-full" required>
              <option value="ONLINE">在线 (ONLINE)</option>
              <option value="OFFLINE">离线 (OFFLINE)</option>
              <option value="WARNING">警告 (WARNING)</option>
              <option value="MAINTENANCE">维护中 (MAINTENANCE)</option>
            </select>
          </div>
        </div>

        <!-- 右侧预览 -->
        <div class="lg:col-span-1 space-y-4 lg:border-l lg:pl-8">
          <h3 class="text-xl font-semibold text-gray-800 border-b border-gray-200 pb-3 mb-4">
            <i class="fa fa-video-camera mr-2 text-primary"></i>连接与预览
          </h3>
          <div class="aspect-video bg-black rounded-lg flex items-center justify-center text-gray-500 overflow-hidden">
            <video v-show="isStreamPlaying" ref="videoPlayer" class="w-full h-full object-cover" controls autoplay muted playsinline></video>
            <div v-if="!isStreamPlaying" class="text-center p-4">
              <i v-if="!isTesting" class="fa fa-power-off text-4xl text-gray-600 mb-2"></i>
              <i v-else class="fa fa-spinner fa-spin text-4xl text-primary mb-2"></i>
              <p>{{ testResult || '暂无预览' }}</p>
            </div>
          </div>
          <button @click="testStream" type="button" class="btn btn-success w-full" :disabled="isTesting || !form.rtspUrl">
            <i class="fa fa-rss mr-2"></i>
            {{ isTesting ? '测试中...' : '开始测试连接' }}
          </button>
          <div class="text-center text-sm font-medium p-2 rounded-md" :class="connectionStatus.bg">
            状态: <span class="font-bold" :class="connectionStatus.text">{{ connectionStatus.label }}</span>
          </div>
        </div>
      </form>

      <!-- 底部按钮 -->
      <div class="mt-8 pt-6 border-t border-gray-200 flex justify-end gap-3">
        <router-link to="/devices" class="btn btn-secondary">取消</router-link>
        <button @click="submitForm" :disabled="isLoading" class="btn btn-primary px-6">
          <i class="fa fa-check mr-2"></i>
          {{ isLoading ? '保存中...' : '确认并保存' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted, computed, reactive, watch} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import apiClient from '@/services/api';
import Hls from 'hls.js';

const route = useRoute();
const props = defineProps({id: String});
const router = useRouter();

const form = ref({
  deviceName: '',
  deviceType: '高清摄像头',
  districtId: null,
  address: '',
  longitude: null,
  latitude: null,
  rtspUrl: '',
  status: 'OFFLINE' // 默认值设为大写
});
const isLoading = ref(false);
const error = ref('');
const districts = ref([]);

const videoPlayer = ref(null);
let hls = null;
const isTesting = ref(false);
const isStreamPlaying = ref(false);
const testResult = ref('');
const connectionStatus = reactive({label: '未连接', text: 'text-gray-700', bg: 'bg-gray-200'});

const isEditMode = computed(() => !!props.id);
const pageTitle = computed(() => isEditMode.value ? '编辑设备信息' : '添加新设备');
const pageDescription = computed(() => "在这里管理设备的详细信息并测试其视频流连接。");

const fetchDistricts = async () => {
  try {
    const response = await apiClient.get('/districts');
    districts.value = response.data;
    if (!isEditMode.value && districts.value.length > 0) {
      form.value.districtId = districts.value[0].districtId;
    }
  } catch (err) {
    console.error("加载辖区列表失败:", err);
  }
};

const loadDeviceData = async () => {
  if (!isEditMode.value) return;
  isLoading.value = true;
  try {
    const response = await apiClient.get(`/devices/${props.id}`);
    form.value = response.data;
  } catch (err) {
    alert('加载设备信息失败');
    router.push('/devices');
  } finally {
    isLoading.value = false;
  }
};

const stopHls = () => {
  if (hls) {
    hls.destroy();
    hls = null;
  }
  isStreamPlaying.value = false;
};

const testStream = () => {
  if (!form.value.rtspUrl) {
    alert('请输入RTSP视频流地址！');
    return;
  }

  try {
    const url = new URL(form.value.rtspUrl);
    const streamPath = url.pathname.startsWith('/') ? url.pathname.substring(1) : url.pathname;
    const hlsUrl = `http://localhost:8888/${streamPath}/index.m3u8`;

    stopHls();
    isTesting.value = true;
    testResult.value = '';
    connectionStatus.label = '正在连接...';
    connectionStatus.text = 'text-yellow-800';
    connectionStatus.bg = 'bg-yellow-100';

    hls = new Hls({lowLatencyMode: true, maxBufferLength: 2, liveSyncDurationCount: 1});
    hls.loadSource(hlsUrl);
    hls.attachMedia(videoPlayer.value);

    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      videoPlayer.value.play().catch(e => console.error("播放失败:", e));
      isTesting.value = false;
      isStreamPlaying.value = true;
      connectionStatus.label = '连接成功';
      connectionStatus.text = 'text-green-800';
      connectionStatus.bg = 'bg-green-100';
      // **关键修正**: 测试成功后，自动更新下拉框的值
      form.value.status = 'ONLINE';
    });

    hls.on(Hls.Events.ERROR, (event, data) => {
      if (data.fatal) {
        isTesting.value = false;
        testResult.value = '无法播放视频流';
        connectionStatus.label = `连接失败: ${data.details}`;
        connectionStatus.text = 'text-red-800';
        connectionStatus.bg = 'bg-red-100';
        // **关键修正**: 测试失败后，自动更新下拉框的值
        form.value.status = 'OFFLINE';
      }
    });
  } catch (e) {
    alert('RTSP地址格式不正确，请输入类似 rtsp://... 的地址');
  }
};

const submitForm = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    // 直接发送表单数据，不再需要解构
    const payload = form.value;

    if (isEditMode.value) {
      await apiClient.put(`/devices/${props.id}`, payload);
    } else {
      await apiClient.post('/devices', payload);
    }
    alert('设备保存成功！');
    router.push('/devices');
  } catch (err) {
    const message = err.response?.data?.message || '操作失败，请检查所有必填项。';
    error.value = message;
    alert(`保存失败: ${message}`);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchDistricts();
  loadDeviceData();
});

watch(() => route.path, stopHls);
onUnmounted(stopHls);

</script>
