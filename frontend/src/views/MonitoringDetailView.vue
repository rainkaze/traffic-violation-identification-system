<template>
  <div v-if="isLoading" class="text-center py-20">
    <p class="text-gray-500">正在加载设备详情...</p>
  </div>
  <div v-else-if="error" class="card bg-red-50 text-red-700 p-4 text-center">
    <p>{{ error }}</p>
  </div>
  <div v-else-if="device" class="p-4">
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
          <video ref="videoPlayer" class="w-full h-full" autoplay playsinline></video>
        </div>
        <div class="flex items-center gap-2 flex-wrap">
          <button @click="startStreaming" class="btn btn-success" :disabled="isConnected || isConnecting">
            <i class="fa fa-play mr-2"></i>{{ isConnecting ? '连接中...' : '开始观看' }}
          </button>
          <button @click="stopStreaming" class="btn btn-danger" :disabled="!isConnected && !isConnecting">
            <i class="fa fa-stop mr-2"></i>停止观看
          </button>
          <span class="text-sm text-gray-500 ml-4">状态: {{ connectionStatus }}</span>
        </div>
      </div>

      <div class="space-y-6">
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">设备信息</h3>
          <dl class="text-sm space-y-2">
            <div class="flex justify-between"><dt class="text-gray-500">设备编码</dt><dd class="font-medium text-gray-800">{{ device.deviceCode }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">设备状态</dt><dd><span class="badge text-white" :class="statusBgColor(device.status)">{{ device.status }}</span></dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">设备类型</dt><dd class="font-medium text-gray-800">{{ device.deviceType }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">所属辖区</dt><dd class="font-medium text-gray-800">{{ device.districtName }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">IP 地址</dt><dd class="font-medium text-gray-800">{{ device.ipAddress || '未配置' }}</dd></div>
            <div class="flex justify-between"><dt class="text-gray-500">设备型号</dt><dd class="font-medium text-gray-800">{{ device.modelName || '未知' }}</dd></div>
          </dl>
        </div>

        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">近期违法抓拍 (模拟)</h3>
          <div class="space-y-3 max-h-60 overflow-y-auto">
            <div v-for="v in recentViolations" :key="v.id" class="flex items-center gap-3 p-2 rounded-lg bg-gray-50 hover:bg-gray-100">
              <img :src="v.img" alt="违法截图" class="w-16 h-12 object-cover rounded">
              <div>
                <p class="font-medium text-sm">{{ v.plate }} - {{ v.type }}</p>
                <p class="text-xs text-gray-500">{{ v.time }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import apiClient from '@/services/api';
import Hls from 'hls.js'; // 导入 Hls.js

const props = defineProps({
  id: {
    type: String,
    required: true,
  }
});

const device = ref(null);
const isLoading = ref(true);
const error = ref(null);

const videoPlayer = ref(null);
let hls = null;
const streamStatus = ref('未连接');
const isPlaying = ref(false);

// 启动视频流
const startStreaming = () => {
  // 这就是您在VLC中成功播放的地址
  const streamName = 'live'; // 这个名称与 OBS RTSP 服务器的路径匹配
  const hlsUrl = `http://localhost:8888/${streamName}/index.m3u8`;

  if (Hls.isSupported()) {
    if (hls) {
      hls.destroy();
    }
    // 为 hls.js 添加低延迟优化配置
    const hlsConfig = {
      lowLatencyMode: true, // 开启低延迟模式
      backBufferLength: 90, // 保留90秒的回看缓冲区
    };
    hls = new Hls(hlsConfig);
    hls.loadSource(hlsUrl);
    hls.attachMedia(videoPlayer.value);
    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      videoPlayer.value.play();
      streamStatus.value = '播放中';
      isPlaying.value = true;
    });
    hls.on(Hls.Events.ERROR, (event, data) => {
      if (data.fatal) {
        console.error('HLS 致命错误:', data);
        streamStatus.value = `错误: ${data.details}`;
        isPlaying.value = false;
        // 尝试恢复
        if(data.type === Hls.ErrorTypes.NETWORK_ERROR) {
          console.log("尝试重新连接...");
          hls.startLoad();
        }
      }
    });
  } else if (videoPlayer.value.canPlayType('application/vnd.apple.mpegurl')) {
    // 对于 Safari 等原生支持HLS的浏览器
    videoPlayer.value.src = hlsUrl;
    videoPlayer.value.addEventListener('loadedmetadata', () => {
      videoPlayer.value.play();
      streamStatus.value = '播放中';
      isPlaying.value = true;
    });
  }
};

// 停止视频流
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

// 在组件卸载时确保清理资源
onUnmounted(() => {
  stopStreaming();
});

// 加载设备基本信息
onMounted(async () => {
  isLoading.value = true;
  try {
    const response = await apiClient.get(`/devices/${props.id}`);
    device.value = response.data;
  } catch (err) {
    console.error("加载设备详情失败:", err);
    error.value = "无法加载设备信息。";
  } finally {
    isLoading.value = false;
  }
});

// 模板中用到的辅助函数
const statusBgColor = (status) => {
  const map = { online: 'bg-success', offline: 'bg-danger', warning: 'bg-warning', maintenance: 'bg-gray-400' };
  return map[status] || 'bg-gray-300';
};
</script>
