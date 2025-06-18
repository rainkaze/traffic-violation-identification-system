
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
            <select class="input w-full sm:w-40 flex-shrink-0">
              <option value="" disabled selected>设备状态</option>
              <option value="online">在线</option>
              <option value="offline">离线</option>
              <option value="error">异常</option>
            </select>

            <!-- 负责人 -->
            <select class="input w-full sm:w-40 flex-shrink-0">
              <option value="" disabled selected>负责人</option>
              <option value="zhangsan">张三</option>
              <option value="lisi">李四</option>
              <option value="wangwu">王五</option>
            </select>

            <!-- 事故等级 -->
            <select class="input w-full sm:w-40 flex-shrink-0">
              <option value="" disabled selected>事故等级</option>
              <option value="level1" class="text-red-500">一级预警</option>
              <option value="level2" class="text-yellow-600">二级预警</option>
              <option value="level3" class="text-blue-600">三级预警</option>
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
          <h3 class="font-bold text-gray-800 mb-4">疑似事故列表</h3>
          <div class="space-y-3">
            <div class="p-3 rounded-lg bg-red-50 border border-red-200 cursor-pointer">
              <p class="font-bold text-red-800">严重事故 - 车辆停滞</p>
              <p class="text-sm text-gray-600">地点: 世纪大道与和平路口</p>
              <p class="text-xs text-gray-500">检测时间: 2025-06-09 14:10</p>
            </div>
            <div class="p-3 rounded-lg bg-yellow-50 border border-yellow-200">
              <p class="font-bold text-yellow-800">轻微事故 - 模拟碰撞信号</p>
              <p class="text-sm text-gray-600">地点: G30高速K3558</p>
              <p class="text-xs text-gray-500">检测时间: 2025-06-09 14:05</p>
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

<script setup>import { onMounted } from 'vue';

// 动态加载百度地图 SDK
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

onMounted(async () => {
  try {
    await loadBaiduMap();

    // 初始化地图
    const map = new BMap.Map("baiduMap");
    const point = new BMap.Point(84.87, 45.59); // 克拉玛依经纬度
    map.centerAndZoom(point, 12);
    map.enableScrollWheelZoom(true);

    // 添加事故点标记
    const accidentPoint = new BMap.Point(84.87, 45.59); // 示例坐标，可替换为真实事故点
    const marker = new BMap.Marker(accidentPoint);
    map.addOverlay(marker);

    // 点击标记弹出信息窗口
    const infoWindow = new BMap.InfoWindow("世纪大道与和平路口\n疑似事故");
    marker.addEventListener("click", () => {
      map.openInfoWindow(infoWindow, accidentPoint);
    });

  } catch (error) {
    console.error('百度地图加载失败:', error);
  }
});
</script>
