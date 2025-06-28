<template>
  <div v-if="isLoading" class="text-center py-20">
    <p class="text-gray-500">正在加载设备详情...</p>
  </div>
  <div v-else-if="error" class="card bg-red-50 text-red-700 p-4 text-center">
    <p>{{ error }}</p>
  </div>
  <div v-else-if="device" class="p-4 sm:p-6 lg:p-8">
    <div class="mb-6 flex items-center justify-between">
      <div>
        <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">{{ device.deviceName }}</h2>
        <p class="text-gray-600">{{ device.address }}</p>
      </div>
      <router-link to="/monitoring" class="btn btn-secondary">
        <i class="fa fa-arrow-left mr-2"></i>返回监控列表
      </router-link>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
      <div class="lg:col-span-3 card p-4">
        <h3 class="font-bold text-lg text-gray-800 mb-4">实时视频流</h3>
        <div class="aspect-video bg-black rounded-lg flex items-center justify-center text-gray-500 mb-4">
          <video ref="videoPlayer" class="w-full h-full" controls autoplay muted playsinline></video>
        </div>
        <div class="flex items-center gap-2 flex-wrap">
          <button @click="startStreaming" class="btn btn-success" :disabled="isPlaying || isLoading">
            <i class="fa fa-play mr-2"></i>开始观看
          </button>
          <button @click="stopStreaming" class="btn btn-danger" :disabled="!isPlaying">
            <i class="fa fa-stop mr-2"></i>停止观看
          </button>
          <span class="text-sm text-gray-500 ml-4">状态: {{ streamStatus }}</span>
        </div>
      </div>

      <div class="lg:col-span-2 space-y-6">
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">设备信息</h3>
          <dl class="text-sm space-y-2">
            <div class="flex justify-between"><dt class="text-gray-500">设备状态</dt><dd><span class="badge text-white" :class="statusBgColor(device.status)">{{ device.status }}</span></dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">设备类型</dt><dd class="font-medium text-gray-800">{{ device.deviceType }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">所属辖区</dt><dd class="font-medium text-gray-800">{{ device.jurisdiction }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">RTSP 地址</dt><dd class="font-medium text-gray-800 truncate" :title="device.rtspUrl">{{ device.rtspUrl || '未配置' }}</dd></div>
          </dl>
        </div>

        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">此设备拍摄到的近期违法记录</h3>
          <div v-if="violationsLoading" class="text-center text-gray-500">
            <p>正在加载违法记录...</p>
          </div>
          <div v-else-if="recentViolations.length === 0" class="text-center text-gray-500">
            <p>该设备暂无近期违法记录。</p>
          </div>
          <div v-else class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
              <tr>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">时间</th>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">车牌</th>
                <th class="px-3 py-2 text-left text-xs font-medium text-gray-500">类型</th>
              </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="violation in recentViolations" :key="violation.id">
                <td class="px-3 py-2 whitespace-nowrap text-sm text-gray-500">{{ formatTime(violation.time) }}</td>
                <td class="px-3 py-2 whitespace-nowrap text-sm font-medium text-gray-800">{{ violation.plate }}</td>
                <td class="px-3 py-2 whitespace-nowrap text-sm text-gray-600">{{ violation.type }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import {useRoute} from 'vue-router';
import apiClient from '@/services/api';
import Hls from 'hls.js';

const props = defineProps({id: String});
const route = useRoute();

// 设备与视频流相关状态
const device = ref(null);
const isLoading = ref(true);
const error = ref(null);
const videoPlayer = ref(null);
let hls = null;
const streamStatus = ref('未连接');
const isPlaying = ref(false);

// 新增：违法记录相关状态
const recentViolations = ref([]);
const violationsLoading = ref(true);

// --- 核心方法 ---
const loadData = async (deviceId) => {
  isLoading.value = true;
  error.value = null;
  // 重置状态
  recentViolations.value = [];
  stopStreaming(); // 停止上一个视频流

  try {
    const deviceResponse = await apiClient.get(`/devices/${deviceId}`);
    device.value = deviceResponse.data;

    const violationsResponse = await apiClient.get(`/devices/${deviceId}/violations`);
    recentViolations.value = violationsResponse.data;
  } catch (err) {
    error.value = "无法加载设备信息，请检查设备ID是否正确或联系管理员。";
    console.error("加载详情页数据失败:", err);
  } finally {
    isLoading.value = false;
  }
};
watch(
  () => props.id,
  (newId) => {
    if (newId) {
      loadData(newId);
    }
  },
  { immediate: true } // 4. immediate: true 确保组件初次加载时也会执行
);
// 获取设备详情
const fetchDeviceDetails = async () => {
  try {
    const response = await apiClient.get(`/devices/${props.id}`);
    device.value = response.data;
  } catch (err) {
    error.value = "无法加载设备信息，请检查设备ID是否正确或联系管理员。";
  } finally {
    isLoading.value = false;
  }
};

// 新增：获取近期违法记录
const fetchRecentViolations = async () => {
  violationsLoading.value = true;
  try {
    const response = await apiClient.get(`/devices/${props.id}/violations`);
    recentViolations.value = response.data;
  } catch (err) {
    console.error("加载违法记录失败:", err);
    // 这里不设置主错误信息，以免覆盖设备加载失败的信息
  } finally {
    violationsLoading.value = false;
  }
};

onMounted(async () => {
  isLoading.value = true;
  // 并行获取设备详情和违法记录
  await Promise.all([
    fetchDeviceDetails(),
    fetchRecentViolations()
  ]);
});

onUnmounted(() => {
  stopStreaming();
});

// --- 视频流控制方法 ---
const startStreaming = () => {
  if (!device.value || !device.value.rtspUrl) {
    alert("未找到该设备的RTSP流地址，无法播放。");
    streamStatus.value = '错误：无RTSP地址';
    return;
  }
  try {
    streamStatus.value = '正在连接...';
// --- 开始替换/修改 ---

// 1. 定义一个函数，用于根据RTSP端口推算HLS端口
    const getHlsPortFromRtspPort = (rtspPort) => {
      // 当地址是 rtsp://localhost/live 时, URL解析出的端口是空字符串 ''
      // RTSP的默认端口是 554
      const port = rtspPort || '554';

      switch (port) {
        case '554': // 第一个摄像头, 对应 mediamtx 1
          return '8888';
        case '555': // 第二个摄像头, 对应 mediamtx 2
          return '9888';
        // case '556': return '10888'; // 如果未来有第三个摄像头
        default:
          // 默认情况, 返回第一个的端口
          return '8888';
      }
    };

// 2. 解析输入的RTSP URL
    const url = new URL(device.value.rtspUrl);
    const streamPath = url.pathname.startsWith('/') ? url.pathname.substring(1) : url.pathname;

// 3. 动态获取HLS端口
    const hlsPort = getHlsPortFromRtspPort(url.port);
   // const hlsPort = 9888;
// 4. 构建最终的、完全动态的HLS URL
    const hlsUrl = `http://localhost:${hlsPort}/${streamPath}/index.m3u8`;
    //const hlsUrl = `http://localhost:9888/${streamPath}/index.m3u8?_t=${new Date().getTime()}`;
// --- 替换/修改结束 ---

    if (Hls.isSupported()) {
      if (hls) hls.destroy();
      hls = new Hls({lowLatencyMode: true, maxBufferLength: 2, liveSyncDurationCount: 1});
      hls.loadSource(hlsUrl);
      hls.attachMedia(videoPlayer.value);

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        videoPlayer.value.play().catch(e => console.error("Autoplay failed:", e));
        streamStatus.value = '播放中';
        isPlaying.value = true;
      });
      hls.on(Hls.Events.ERROR, (event, data) => {
        console.error('HLS Player Error:', data);
        streamStatus.value = `播放失败: ${data.details}`;
        isPlaying.value = false;
      });
    }
  } catch (e) {
    alert("设备的RTSP地址格式不正确，无法播放。");
    streamStatus.value = '错误：RTSP地址无效';
  }
};

const stopStreaming = () => {
  if (hls) {
    hls.destroy();
    hls = null;
  }
  if (videoPlayer.value) {
    videoPlayer.value.pause();
    videoPlayer.value.src = '';
  }
  streamStatus.value = '已停止';
  isPlaying.value = false;
};

// --- 辅助函数 ---
const statusBgColor = (status) => {
  const map = {
    online: 'bg-success',
    offline: 'bg-danger',
    warning: 'bg-warning',
    maintenance: 'bg-gray-400'
  };
  return map[status.toLowerCase()] || 'bg-gray-300';
};

// 新增：格式化时间
const formatTime = (isoString) => {
  if (!isoString) return 'N/A';
  const date = new Date(isoString);
  // 返回如 "06-10 15:30" 的格式
  return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};
</script>
