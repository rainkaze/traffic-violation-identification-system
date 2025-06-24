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
          <!-- 设备信息显示框 -->
          <div v-if="selectedDeviceInfo" class="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-md">
            <p class="text-sm font-medium text-blue-800">
              已选设备：设备ID: {{ selectedDeviceInfo.deviceId }}，设备类型: {{ selectedDeviceInfo.deviceType }}
            </p>
          </div>
          <!-- 下拉菜单 -->
          <div class="mb-4">
            <label for="accidentSelect" class="block text-sm font-medium text-gray-700 mb-2">选择事故</label>
            <select
              id="accidentSelect"
              v-model="selectedViolationId"
              class="input w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="" disabled selected>请选择事故</option>
              <option v-for="violation in violationsByDevice" :key="violation.deviceId" :value="violation.violationId">
                {{ violation.plateNumber }} - {{ formatTime(violation.violationTime) }}
              </option>
            </select>
          </div>
          <div class="space-y-3">
            <!-- 事故等级选择 -->
            <div class="mb-4">
              <label for="accidentLevelSelect" class="block text-sm font-medium text-gray-700 mb-2">事故等级</label>
              <select
                id="accidentLevelSelect"
                v-model="selectedAccidentLevel"
                class="input w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="" disabled selected>请选择事故等级</option>
                <option value="level1">一级事故</option>
                <option value="level2">二级事故</option>
                <option value="level3">三级事故</option>
              </select>
            </div>

            <!-- 动态事故条目 -->
            <div
              v-if="selectedAccidentInfo"
              class="p-3 rounded-lg border"
              :class="{
                'bg-red-50 border-red-200': selectedAccidentLevel === 'level1',
                'bg-yellow-50 border-yellow-200': selectedAccidentLevel === 'level2',
                'bg-blue-50 border-blue-200': selectedAccidentLevel === 'level3'
              }"
            >
              <p
                class="font-bold"
                :class="{
                'text-red-800': selectedAccidentLevel === 'level1',
                'text-yellow-800': selectedAccidentLevel === 'level2',
                'text-blue-800': selectedAccidentLevel === 'level3'
              }"
              >
                事故等级:
                <span
                  class="inline-block px-2 py-1 text-xs font-semibold rounded-full ml-2"
                  :class="{
                  'bg-red-100 text-red-800': selectedAccidentLevel === 'level1',
                  'bg-yellow-100 text-yellow-800': selectedAccidentLevel === 'level2',
                  'bg-blue-100 text-blue-800': selectedAccidentLevel === 'level3'
                }"
                >
                  {{
                    selectedAccidentLevel === 'level1' ? '一级事故' :
                      selectedAccidentLevel === 'level2' ? '二级事故' :
                        selectedAccidentLevel === 'level3' ? '三级事故' : ''
                  }}
                </span>
              </p>

              <p class="text-sm text-gray-600">事故地点: {{ selectedAccidentInfo.address }}</p>
              <p class="text-xs text-gray-500">检测时间: {{ formatTime(selectedAccidentInfo.violationTime) }}</p>
              <p class="text-xs text-gray-500">事故状态: {{ selectedAccidentInfo.violationStatus }}</p>
            </div>

            <!-- 默认提示 -->
            <div v-else class="p-3 rounded-lg bg-gray-100 border border-gray-300 text-gray-500">
              请选择一个事故以查看详细信息
            </div>
          </div>
        </div>

        <!-- 卡片容器 -->
        <div class="card">
          <!-- 新增按钮到卡片顶部 -->
          <div class="mb-4 relative">
            <button
              @click="generateStrategyWithAI"
              :disabled="isGenerating || !selectedAccidentInfo || !selectedAccidentLevel"
              class="btn btn-success w-full"
            >
              <i class="fa fa-magic mr-2"></i>
              {{ isGenerating ? '生成中...' : '智能生成处置策略' }}
            </button>
            <div v-if="isGenerating" class="my-2 text-blue-600 font-medium flex items-center gap-2">
              <svg class="animate-spin h-5 w-5 text-blue-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"></path>
              </svg>
              正在智能生成处置策略，请稍候...
            </div>
          </div>

          <h3 class="font-bold text-gray-800 mb-4">处置策略生成</h3>
          <div class="space-y-4">
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="font-medium mb-2">智能生成的处置指令:</p>
              <div class="text-sm text-gray-700 whitespace-pre-line" v-if="generatedStrategy">
                {{ generatedStrategy }}
              </div>
              <div v-else class="text-sm text-gray-400 italic">
                暂无生成内容，请点击上方按钮生成处置策略。
              </div>
            </div>

            <!-- 可选：用于调试的状态输出 -->
            <!-- <pre>调试状态: {{ { isGenerating, selectedAccidentInfo, selectedAccidentLevel } }}</pre> -->
            <div class="flex gap-2">
              <button class="btn btn-primary w-full" @click="showConfirmDialog = true">
                <i class="fa fa-map-signs mr-2"></i>推送策略至App</button>

              <!-- 确认对话框 -->
              <div v-if="showConfirmDialog" class="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
                <div class="bg-white rounded-lg shadow-lg p-6 w-96">
                  <h3 class="text-lg font-bold mb-4">确认操作</h3>
                  <p class="mb-6">您确定要推送处置策略至App吗？</p>
                  <div class="flex justify-end gap-4">
                    <button @click="showConfirmDialog = false" class="btn btn-secondary">取消</button>
                    <button @click="confirmPushStrategy" class="btn btn-primary">确定</button>
                  </div>
                </div>
              </div>

              <button class="btn btn-warning w-full" @click="showTrafficLightDialog = true">
                <i class="fa fa-lightbulb-o mr-2"></i>执行信号灯联动</button>

              <!-- 信号灯联动对话框 -->
              <div v-if="showTrafficLightDialog" class="fixed inset-0 flex items-center justify-center z-50 bg-black bg-opacity-50">
                <div class="bg-white rounded-lg shadow-lg p-6 w-96">
                  <h3 class="text-lg font-bold mb-4">信号灯联动</h3>
                  <p class="mb-6">已经通知相关部门，对救援车辆优先放行</p>
                  <div class="flex justify-end">
                    <button @click="showTrafficLightDialog = false" class="btn btn-primary">确定</button>
                  </div>
                </div>
              </div>
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

// 响应式变量声明
const selectedDeviceInfo = ref(null); // 存储当前选中的设备信息
const selectedAccidentInfo = ref(null); // 当前选中的事故详情
const violationsByDevice = ref([]);  // 设备下的违法记录
const selectedViolationId = ref(''); // 下拉框当前选中项
let devices = []; // 全局保存设备列表

const generatedStrategy = ref('');    // AI 生成的策略
const isGenerating = ref(false);     // 加载状态
const selectedStatus = ref('');
const selectedDistrict = ref('');
const selectedAccidentLevel = ref(''); // 事故等级选择器
const showConfirmDialog = ref(false); // 控制确认对话框显示
const showTrafficLightDialog = ref(false); // 控制信号灯联动对话框显示

let map; // 在顶层定义 map 变量

const formatTime = (time) => {
  if (!time) return '';
  return new Date(time).toLocaleString();
};

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

  const size = 25;
  canvas.width = size;
  canvas.height = size;

  ctx.beginPath();
  ctx.arc(size / 2, size / 2, size / 2 - 2, 0, Math.PI * 2);
  ctx.fillStyle = '#FF0000';
  ctx.fill();

  ctx.beginPath();
  ctx.arc(size / 2, size / 2, (size / 2) * 0.6, 0, Math.PI * 2);
  ctx.fillStyle = '#FFFFFF';
  ctx.fill();

  return new BMap.Icon(canvas.toDataURL(), new BMap.Size(size, size), {
    imageSize: new BMap.Size(size, size),
    anchor: new BMap.Size(size / 2, size)
  });
};


onMounted(async () => {
  try {
    await loadBaiduMap();

    map = new BMap.Map("baiduMap");
    const point = new BMap.Point(84.87, 45.59);
    map.centerAndZoom(point, 12);
    map.enableScrollWheelZoom(true);

    const response = await apiClient.get('/accidents/devices');
    devices = response.data;

    devices.forEach(device => {
      if (!device.longitude || !device.latitude) return;

      const devicePoint = new BMap.Point(device.longitude, device.latitude);
      const marker = new BMap.Marker(devicePoint, { icon: createDefaultStyleMarkerIcon() });

      map.addOverlay(marker);

      function createInfoWindowContent(device) {
        return `
          <div class="p-2">
            <p class="font-bold">${device.deviceName}</p>
            <p>设备编号: ${device.deviceCode}</p>
            <p>类型: ${device.deviceType}</p>
            <p>地址: ${device.address}</p>
            <p>状态: ${device.status}</p>
            <button class="mt-2 btn btn-xs btn-primary select-device-btn" data-device='${JSON.stringify(device)}'>选择该设备</button>
          </div>
        `;
      }

      marker.addEventListener("click", () => {
        const content = createInfoWindowContent(device);
        const infoWindow = new BMap.InfoWindow(content);
        map.openInfoWindow(infoWindow, devicePoint);

        setTimeout(() => {
          const button = document.querySelector('.select-device-btn');
          if (button) {
            button.addEventListener('click', () => {
              const deviceData = JSON.parse(button.getAttribute('data-device'));
              selectedDeviceInfo.value = {
                deviceId: deviceData.deviceId,
                deviceType: deviceData.deviceType
              };

              const filteredViolations = devices
                .filter(d => d.deviceId === deviceData.deviceId)
                .filter(d => d.plateNumber && d.violationTime);

              violationsByDevice.value = filteredViolations;
              selectedViolationId.value = '';
            });
          }
        }, 100);
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

    map.clearOverlays();

    devices.forEach(device => {
      if (!device.longitude || !device.latitude) return;

      const devicePoint = new BMap.Point(device.longitude, device.latitude);
      const marker = new BMap.Marker(devicePoint, { icon: createDefaultStyleMarkerIcon() });
      map.addOverlay(marker);

      function createInfoWindowContent(device) {
        return `
          <div class="p-2">
            <p class="font-bold">${device.deviceName}</p>
            <p>设备编号: ${device.deviceCode}</p>
            <p>类型: ${device.deviceType}</p>
            <p>地址: ${device.address}</p>
            <p>状态: ${device.status}</p>
            <button class="mt-2 btn btn-xs btn-primary select-device-btn" data-device='${JSON.stringify(device)}'>选择该设备</button>
          </div>
        `;
      }

      marker.addEventListener("click", () => {
        const content = createInfoWindowContent(device);
        const infoWindow = new BMap.InfoWindow(content);
        map.openInfoWindow(infoWindow, devicePoint);

        setTimeout(() => {
          const button = document.querySelector('.select-device-btn');
          if (button) {
            button.addEventListener('click', () => {
              const deviceData = JSON.parse(button.getAttribute('data-device'));
              selectedDeviceInfo.value = {
                deviceId: deviceData.deviceId,
                deviceType: deviceData.deviceType
              };

              const filteredViolations = devices
                .filter(d => d.deviceId === deviceData.deviceId)
                .filter(d => d.plateNumber && d.violationTime);

              violationsByDevice.value = filteredViolations;
              selectedViolationId.value = '';
            });
          }
        }, 100);
      });
    });
  } catch (error) {
    console.error('设备数据加载失败:', error);
  }
});

watch(selectedViolationId, () => {
  if (!selectedViolationId.value) {
    selectedAccidentInfo.value = null;
    return;
  }

  const selected = violationsByDevice.value.find(
    v => v.violationId === Number(selectedViolationId.value)
  );

  selectedAccidentInfo.value = selected;
});

// AI 生成处置策略方法
const generateStrategyWithAI = async () => {
  console.log('generateStrategyWithAI 被调用'); // 添加这行日志
  if (!selectedAccidentInfo.value || !selectedAccidentLevel.value) {
    alert('请先选择事故和事故等级');
    return;
  }

  const location = selectedAccidentInfo.value?.address || '未知地点';
  const levelText =
    selectedAccidentLevel.value === 'level1' ? '一级事故' :
      selectedAccidentLevel.value === 'level2' ? '二级事故' :
        selectedAccidentLevel.value === 'level3' ? '三级事故' : '未指定';

  const prompt = `"${location}"发生了交通事故，事故等级为"${levelText}"，请你为我生成绕行路线和处置策略，内容控制在300字以内`;

  isGenerating.value = true;
  generatedStrategy.value = '';

  try {
    const response = await fetch("https://api.deepseek.com/chat/completions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer sk-c87575e411794a84b99dfbfd4d706052"
      },
      body: JSON.stringify({
        model: "deepseek-chat",
        messages: [{ role: "user", content: prompt }],
        max_tokens: 500,
        temperature: 0.7
      })
    });

    const data = await response.json();
    if (data && data.choices && data.choices.length > 0) {
      generatedStrategy.value = data.choices[0].message.content.trim();
    } else {
      generatedStrategy.value = '未能生成有效策略，请重试。';
    }
  } catch (error) {
    console.error("调用 Deepseek 失败:", error);
    generatedStrategy.value = '调用 AI 服务失败，请检查网络或稍后再试。';
  } finally {
    isGenerating.value = false;
  }
};

// 确认推送策略方法
const confirmPushStrategy = () => {
  if (!generatedStrategy.value) {
    alert('没有可推送的处置策略');
    return;
  }

  // 这里可以添加实际的推送逻辑，例如调用API
  console.log('推送处置策略:', generatedStrategy.value);
  alert('处置策略已成功推送至App');
  showConfirmDialog.value = false; // 关闭对话框
};

</script>

