<template>
  <div class="p-4 sm:p-6 lg:p-8">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">WebRTC 连接测试</h2>
      <p class="text-gray-600">本页面用于验证Android摄像头端与Web浏览器端的直接视频流连接。</p>
    </div>

    <div class="card max-w-4xl mx-auto">
      <div class="mb-6">
        <label for="deviceIdInput" class="block text-sm font-medium text-gray-700">1. 输入目标设备ID</label>
        <p class="text-xs text-gray-500 mb-2">请输入您在Android应用上注册后看到的设备ID。</p>
        <input
          v-model="targetDeviceId"
          id="deviceIdInput"
          type="text"
          class="input w-full md:w-1/2"
          placeholder="例如: 5"
          :disabled="isConnecting || isConnected"
        />
      </div>

      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700">2. 控制连接</label>
        <div class="flex items-center gap-4 mt-2">
          <button @click="startConnection" class="btn btn-success" :disabled="!targetDeviceId || isConnecting || isConnected">
            <i class="fa fa-play-circle mr-2"></i>{{ isConnecting ? '连接中...' : '开始连接' }}
          </button>
          <button @click="stopConnection" class="btn btn-danger" :disabled="!isConnected && !isConnecting">
            <i class="fa fa-stop-circle mr-2"></i>断开连接
          </button>
          <div class="text-sm font-medium">
            状态: <span :class="statusColor">{{ connectionStatus }}</span>
          </div>
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700">3. 视频流画面</label>
        <div class="mt-2 aspect-video bg-black rounded-lg flex items-center justify-center text-gray-500">
          <video ref="videoPlayer" class="w-full h-full" autoplay playsinline muted></video>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue';

const targetDeviceId = ref('');
const videoPlayer = ref(null);

let peerConnection = null;
let webSocket = null;
let viewerId = '';

const connectionStatus = ref('未连接');
const isConnecting = ref(false);
const isConnected = ref(false);

const statusColor = computed(() => {
  switch (connectionStatus.value) {
    case 'connected':
      return 'text-success';
    case 'failed':
    case 'disconnected':
    case 'closed':
    case '信令错误':
      return 'text-danger';
    default:
      return 'text-warning';
  }
});

const ICE_SERVERS = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' }
  ]
};

const createPeerConnection = () => {
  console.log('[WebRTC] Creating PeerConnection');
  peerConnection = new RTCPeerConnection(ICE_SERVERS);

  peerConnection.onicecandidate = (event) => {
    if (event.candidate && webSocket && webSocket.readyState === WebSocket.OPEN) {
      webSocket.send(JSON.stringify({
        type: 'ice-candidate',
        candidate: event.candidate,
        targetDeviceId: targetDeviceId.value,
      }));
    }
  };

  peerConnection.ontrack = (event) => {
    console.log('[WebRTC] Remote track received!');
    if (videoPlayer.value && event.streams[0]) {
      videoPlayer.value.srcObject = event.streams[0];
    }
  };

  peerConnection.onconnectionstatechange = () => {
    console.log(`[WebRTC] Connection state changed: ${peerConnection.connectionState}`);
    connectionStatus.value = peerConnection.connectionState;
    isConnected.value = peerConnection.connectionState === 'connected';
    isConnecting.value = peerConnection.connectionState === 'connecting';
  };
};

const setupWebSocket = () => {
  viewerId = `viewer-test-${Math.random().toString(36).substr(2, 9)}`;
  const wsUrl = `ws://localhost:8080/api/signal?deviceId=${viewerId}`;

  console.log(`[WebSocket] Connecting to ${wsUrl}`);
  webSocket = new WebSocket(wsUrl);

  webSocket.onopen = async () => {
    connectionStatus.value = '信令已连接，正在协商...';
    if (!peerConnection) createPeerConnection();

    try {
      console.log('[WebRTC] Creating offer...');
      const offer = await peerConnection.createOffer();
      await peerConnection.setLocalDescription(offer);

      console.log(`[WebSocket] Sending 'offer' to target [${targetDeviceId.value}]`);
      webSocket.send(JSON.stringify({
        type: 'offer',
        sdp: peerConnection.localDescription,
        targetDeviceId: targetDeviceId.value,
      }));
    } catch (e) {
      console.error("创建Offer失败: ", e);
      connectionStatus.value = '创建Offer失败';
      isConnecting.value = false;
    }
  };

  webSocket.onmessage = async (event) => {
    const message = JSON.parse(event.data);
    console.log('[WebSocket] Message received:', message.type);
    if (!peerConnection) return;
    try {
      if (message.type === 'answer') {
        console.log('[WebRTC] Received "answer", setting remote description.');
        await peerConnection.setRemoteDescription(new RTCSessionDescription(message.sdp));
      } else if (message.type === 'ice-candidate') {
        console.log('[WebRTC] Received "ice-candidate", adding candidate.');
        await peerConnection.addIceCandidate(new RTCIceCandidate(message.candidate));
      }
    } catch (e) {
      console.error("处理信令消息失败: ", e);
    }
  };

  webSocket.onclose = () => {
    if (connectionStatus.value !== 'connected') {
      connectionStatus.value = '信令连接失败';
    }
    isConnecting.value = false;
  };

  webSocket.onerror = (err) => {
    console.error('WebSocket 错误:', err);
    connectionStatus.value = '信令错误';
    isConnecting.value = false;
  };
};

const startConnection = () => {
  if (!targetDeviceId.value) {
    alert('请输入目标设备ID');
    return;
  }
  if (isConnecting.value || isConnected.value) return;
  isConnecting.value = true;
  connectionStatus.value = '正在连接...';
  setupWebSocket();
};

const stopConnection = () => {
  if (peerConnection) {
    peerConnection.close();
    peerConnection = null;
  }
  if (webSocket) {
    webSocket.close();
    webSocket = null;
  }
  if (videoPlayer.value) {
    videoPlayer.value.srcObject = null;
  }
  connectionStatus.value = '未连接';
  isConnected.value = false;
  isConnecting.value = false;
  console.log('[WebRTC] All connections stopped.');
};

onUnmounted(stopConnection);
</script>
