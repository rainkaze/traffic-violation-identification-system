<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">事故处置引导 (模拟功能)</h2>
      <p class="text-gray-600">模拟事故检测、策略生成和处置引导</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="card lg:col-span-2">
        <!-- 标题 + 筛选框 容器 -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between mb-4">
          <h3 class="font-bold text-gray-800 text-lg mb-3 sm:mb-0">克拉玛依市交通态势图</h3>

          <!-- 筛选框容器 -->
          <div class="flex flex-nowrap gap-3 overflow-x-auto">
            <!-- 设备状态 -->
            <select class="input w-full sm:w-40 flex-shrink-0" v-model="selectedStatus">
              <option value="" disabled selected>设备状态</option>
              <option value="online">online</option>
              <option value="offline">offline</option>
              <option value="warning">warning</option>
              <option value="maintenance">maintenance</option>
            </select>

            <!-- 辖区 -->
            <select class="input w-full sm:w-40 flex-shrink-0" v-model="selectedDistrict">
              <option value="" disabled selected>辖区</option>
              <option value="克拉玛依区">克拉玛依区</option>
              <option value="独山子区">独山子区</option>
              <option value="白碱滩区">白碱滩区</option>
            </select>
          </div>
        </div>

        <!-- 百度地图容器 -->
        <div class="relative w-full h-[600px] bg-gray-200 rounded-lg flex items-center justify-center overflow-hidden">
          <div id="baiduMap" class="w-full h-full"></div>
        </div>
      </div>

      <div class="space-y-6">
        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">事故详情</h3>
          <!-- 下拉菜单 -->
          <div class="mb-4">
            <label for="accidentSelect" class="block text-sm font-medium text-gray-700 mb-2">选择事故</label>
            <select id="accidentSelect" class="input w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500">
              <option value="" disabled selected>请选择事故</option>
              <option value="accident1">严重事故 - 世纪大道与和平路口</option>
              <option value="accident2">轻微事故 - G30高速K3558</option>
            </select>
          </div>
          <div class="space-y-3">
            <!-- 示例事故条目 -->
            <div class="p-3 rounded-lg bg-red-50 border border-red-200 cursor-pointer">
              <p class="font-bold text-red-800">事故等级: 一级事故</p>
              <p class="text-sm text-gray-600">事故地点: 世纪大道与和平路口</p>
              <p class="text-sm text-gray-600">负责人: 张三</p>
              <p class="text-xs text-gray-500">检测时间: 2025-06-09 14:10</p>
              <p class="text-xs text-gray-500">事故状态: 进行中</p>
            </div>
          </div>
        </div>

        <div class="card">
          <h3 class="font-bold text-gray-800 mb-4">处置策略生成</h3>
          <div class="space-y-4">
            <div>
              <p class="font-medium text-gray-700">事故类型: <span class="font-bold text-danger">严重事故</span></p>
              <p class="text-sm text-gray-500">策略库匹配: 严重事故处置流程</p>
            </div>
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="font-medium mb-2">处置指令:</p>
              <ol class="list-decimal list-inside space-y-2 text-sm text-gray-700">
                <li><span class="font-bold">生成绕行路线:</span> 建议由世纪大道 -> 迎宾路 -> 友谊路绕行。</li>
                <li><span class="font-bold">通知相关部门:</span> 已模拟发送通知至医疗、保险部门。</li>
                <li><span class="font-bold">交通信号联动:</span> 准备调整事故点周边信号灯。</li>
              </ol>
            </div>
            <div class="flex gap-2">
              <button class="btn btn-primary w-full"><i class="fa fa-map-signs mr-2"></i>推送绕行路线至App</button>
              <button class="btn btn-warning w-full"><i class="fa fa-lightbulb-o mr-2"></i>执行信号灯联动</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import apiClient from '@/services/api';

// 筛选条件
const selectedStatus = ref('');
const selectedDistrict = ref('');

let map; // 在顶层定义 map 变量

const loadBaiduMap = () => {
  return new Promise((resolve, reject) => {
    if (window.BMap) {
      resolve(window.BMap);
      return;
    }

    const script = document.createElement('script');
    script.src = `https://api.map.baidu.com/api?v=3.0&ak=N2K7edOe0MaKCWvY3a1uLJphfommaMKJ&callback=onBMapLoaded`;
    script.onerror = reject;
    window.onBMapLoaded = () => {
      delete window.onBMapLoaded;
      resolve(window.BMap);
    };
    document.head.appendChild(script);
  });
};

const createDefaultStyleMarkerIcon = () => {
  const canvas = document.createElement('canvas');
  const ctx = canvas.getContext('2d');

  // 设置图标大小（放大一倍）
  const size = 25;
  canvas.width = size;
  canvas.height = size;

  // 绘制红色圆底
  ctx.beginPath();
  ctx.arc(size / 2, size / 2, size / 2 - 2, 0, Math.PI * 2);
  ctx.fillStyle = '#FF0000';
  ctx.fill();

  // 白色中心小圆
  ctx.beginPath();
  ctx.arc(size / 2, size / 2, (size / 2) * 0.6, 0, Math.PI * 2);
  ctx.fillStyle = '#FFFFFF';
  ctx.fill();

  // 返回 BMap.Icon 对象
  return new BMap.Icon(canvas.toDataURL(), new BMap.Size(size, size), {
    imageSize: new BMap.Size(size, size),
    anchor: new BMap.Size(size / 2, size) // 锚点在底部中间
  });
};


onMounted(async () => {
  try {
    await loadBaiduMap();

    map = new BMap.Map("baiduMap"); // 将 map 赋值给顶层变量
    const point = new BMap.Point(84.87, 45.59); // 克拉玛依经纬度
    map.centerAndZoom(point, 12);
    map.enableScrollWheelZoom(true);

    const response = await apiClient.get('/accidents/devices');
    const devices = response.data;

    devices.forEach(device => {
      if (!device.longitude || !device.latitude) return;

      const devicePoint = new BMap.Point(device.longitude, device.latitude);

      // 使用我们自定义的大号默认风格图标
      const marker = new BMap.Marker(devicePoint, { icon: createDefaultStyleMarkerIcon() });

      map.addOverlay(marker);

      const infoWindow = new BMap.InfoWindow(`    <div class="p-2">
      <p class="font-bold">${device.deviceName}</p>
      <p>设备编号: ${device.deviceCode}</p>
      <p>类型: ${device.deviceType}</p>
      <p>地址: ${device.address}</p>
      <p>状态: ${device.status}</p>
    </div>
  `);

      marker.addEventListener("click", () => {
        map.openInfoWindow(infoWindow, devicePoint);
      });
    });

  } catch (error) {
    console.error('百度地图或设备数据加载失败:', error.response || error.message || error);
  }
});

watch([selectedStatus, selectedDistrict], async () => {
  try {
    const params = {};
    if (selectedStatus.value) params.status = selectedStatus.value;
    if (selectedDistrict.value) params.districtName = selectedDistrict.value;

    const response = await apiClient.get('/accidents/devices', { params });
    const devices = response.data;

    // 清除原有标记
    map.clearOverlays(); // 使用顶层变量 map

    // 添加新标记
    devices.forEach(device => {
      if (!device.longitude || !device.latitude) return;

      const devicePoint = new BMap.Point(device.longitude, device.latitude);
      const marker = new BMap.Marker(devicePoint, { icon: createDefaultStyleMarkerIcon() });
      map.addOverlay(marker);

      const infoWindow = new BMap.InfoWindow(`        <div class="p-2">
          <p class="font-bold">${device.deviceName}</p>
          <p>设备编号: ${device.deviceCode}</p>
          <p>类型: ${device.deviceType}</p>
          <p>地址: ${device.address}</p>
          <p>状态: ${device.status}</p>
        </div>
      `);

      marker.addEventListener("click", () => {
        map.openInfoWindow(infoWindow, devicePoint);
      });
    });

  } catch (error) {
    console.error('设备数据加载失败:', error);
  }
});
</script>


