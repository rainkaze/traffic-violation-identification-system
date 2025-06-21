// 文件路径: frontend/src/composables/useWebRTC.js

import { ref, onUnmounted, computed } from 'vue';

export function useWebRTC() {
  const videoPlayer = ref(null);
  const connectionStatus = ref('未连接');
  const isConnected = ref(false);
  const isConnecting = ref(false);

  let peerConnection = null;
  let webSocket = null;
  let viewerId = '';
  let targetDeviceIdCache = '';

  const statusColor = computed(() => {
    if (isConnected.value) return 'text-success';
    if (connectionStatus.value === 'failed' || connectionStatus.value === 'disconnected' || connectionStatus.value.includes('错误')) return 'text-danger';
    return 'text-warning';
  });

  // [最终修正] 使用包含STUN和TURN的更强大的服务器列表
  const ICE_SERVERS = {
    iceServers: [
      // 首先尝试STUN
      { urls: "stun:47.94.105.113:3478" }, // <-- 【请修改】为您服务器的公网IP

      // 如果STUN失败，使用您的TURN服务器作为中继
      {
        urls: "turn:47.94.105.113:3478", // <-- 【请修改】为您服务器的公网IP
        username: "qwer",               // <-- 【请修改】为您在turnserver.conf中设置的用户名
        credential: "1234"              // <-- 【请修改】为您设置的密码
      },
    ],
    iceCandidatePoolSize: 10
  };
  const createPeerConnection = () => {
    peerConnection = new RTCPeerConnection(ICE_SERVERS);

    peerConnection.onicecandidate = (event) => {
      if (event.candidate && webSocket && webSocket.readyState === WebSocket.OPEN) {
        webSocket.send(JSON.stringify({
          type: 'ice-candidate',
          targetDeviceId: targetDeviceId,
          candidate: event.candidate,
        }));
      }
    };

    peerConnection.ontrack = (event) => {
      if (videoPlayer.value && event.streams[0]) {
        videoPlayer.value.srcObject = event.streams[0];
      }
    };

    peerConnection.onconnectionstatechange = () => {
      if (!peerConnection) return;
      connectionStatus.value = peerConnection.connectionState;
      isConnected.value = peerConnection.connectionState === 'connected';
      isConnecting.value = ['connecting', 'new', 'checking'].includes(peerConnection.connectionState);
    };
  };

  const startViewer = (targetDeviceId) => {
    if (!targetDeviceId || isConnecting.value || isConnected.value) return;

    targetDeviceIdCache = targetDeviceId;
    viewerId = `viewer-${Math.random().toString(36).substr(2, 9)}`;
    const wsUrl = `ws://localhost:8080/api/signal?deviceId=${viewerId}`;

    isConnecting.value = true;
    connectionStatus.value = '正在连接信令服务器...';

    webSocket = new WebSocket(wsUrl);

    webSocket.onopen = async () => {
      connectionStatus.value = '信令已连接, 正在协商...';
      if (!peerConnection) createPeerConnection();

      try {
        const offer = await peerConnection.createOffer();
        await peerConnection.setLocalDescription(offer);
        webSocket.send(JSON.stringify({
          type: 'offer',
          sdp: peerConnection.localDescription,
          targetDeviceId: targetDeviceId,
        }));
      } catch (e) {
        console.error("创建Offer失败: ", e);
        connectionStatus.value = '创建Offer失败';
        isConnecting.value = false;
      }
    };

    webSocket.onmessage = async (event) => {
      const message = JSON.parse(event.data);
      if (!peerConnection) return;
      try {
        if (message.type === 'answer') {
          await peerConnection.setRemoteDescription(new RTCSessionDescription(message.sdp));
        } else if (message.type === 'ice-candidate' && message.fromId !== viewerId) {
          await peerConnection.addIceCandidate(new RTCIceCandidate(message.candidate));
        }
      } catch (e) {
        console.error("处理信令消息失败: ", e);
      }
    };

    webSocket.onerror = () => {
      connectionStatus.value = '信令错误';
      isConnecting.value = false;
    };

    webSocket.onclose = () => {
      if (isConnected.value) {
        connectionStatus.value = '已断开';
      } else {
        connectionStatus.value = '信令连接失败';
      }
      isConnecting.value = false;
      isConnected.value = false;
    };
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
  };

  onUnmounted(stopConnection);

  return {
    videoPlayer,
    connectionStatus,
    statusColor,
    isConnected,
    isConnecting,
    startViewer,
    stopConnection,
  };
}
