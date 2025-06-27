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

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 card p-4">
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

      <div class="space-y-6">
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">设备信息</h3>
          <dl class="text-sm space-y-2">
            <div class="flex justify-between"><dt class="text-gray-500">设备状态</dt><dd><span class="badge text-white" :class="statusBgColor(device.status)">{{ device.status }}</span></dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">设备类型</dt><dd class="font-medium text-gray-800">{{ device.deviceType }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">所属辖区</dt><dd class="font-medium text-gray-800">{{ device.districtName }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">RTSP 地址</dt><dd class="font-medium text-gray-800 truncate" :title="device.rtspUrl">{{ device.rtspUrl || '未配置' }}</dd></div>
          </dl>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted} from 'vue';
import {useRoute} from 'vue-router';
import apiClient from '@/services/api';
import Hls from 'hls.js';

const props = defineProps({id: String});
const route = useRoute();

const device = ref(null);
const isLoading = ref(true);
const error = ref(null);

const videoPlayer = ref(null);
let hls = null;
const streamStatus = ref('未连接');
const isPlaying = ref(false);

const startStreaming = () => {
  if (!device.value || !device.value.rtspUrl) {
    alert("未找到该设备的RTSP流地址，无法播放。");
    streamStatus.value = '错误：无RTSP地址';
    return;
  }

  try {
    streamStatus.value = '正在连接...';
    const url = new URL(device.value.rtspUrl);
    const streamPath = url.pathname.startsWith('/') ? url.pathname.substring(1) : url.pathname;
    const hlsUrl = `http://localhost:8888/${streamPath}/index.m3u8`;

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

onMounted(async () => {
  isLoading.value = true;
  try {
    const response = await apiClient.get(`/devices/${props.id}`);
    device.value = response.data;
  } catch (err) {
    error.value = "无法加载设备信息，请检查设备ID是否正确或联系管理员。";
  } finally {
    isLoading.value = false;
  }
});

onUnmounted(stopStreaming);

// 辅助函数
const statusBgColor = (status) => {
  const map = {
    online: 'bg-success',
    offline: 'bg-danger',
    warning: 'bg-warning',
    maintenance: 'bg-gray-400'
  };
  return map[status.toLowerCase()] || 'bg-gray-300';
};
</script>
