<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">实时监控</h2>
      <p class="text-gray-600">查看克拉玛依市各路口实时监控和违法行为检测</p>
    </div>

    <div class="card mb-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
        <div class="flex flex-col sm:flex-row gap-3">
          <select class="input w-full sm:w-60">
            <option value="">全部区域</option>
            <option value="kmq">克拉玛依区</option>
            <option value="dsz">独山子区</option>
            <option value="bjt">白碱滩区</option>
            <option value="weh">乌尔禾区</option>
          </select>
          <select class="input w-full sm:w-60">
            <option value="">全部设备</option>
            <option value="cam001">KMQ-CAM-001 (世纪大道与友谊路口)</option>
            <option value="cam002">GS-RADAR-002 (G30高速K3550)</option>
            <option value="cam003">BJT-CAM-003 (和平路)</option>
          </select>
        </div>
        <div class="flex gap-2">
          <button class="btn btn-secondary">
            <i class="fa fa-refresh mr-1"></i> 刷新
          </button>
          <button class="btn btn-primary">
            <i class="fa fa-video-camera mr-1"></i> 全部直播
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div class="card bg-gray-900 p-0 overflow-hidden">
          <div class="relative">
            <img src="https://picsum.photos/id/1016/800/450" alt="监控画面" class="w-full h-48 object-cover">
            <div class="absolute top-2 left-2 bg-danger/80 text-white text-xs px-2 py-1 rounded">
              <i class="fa fa-circle mr-1 animate-pulse"></i> 直播中
            </div>
            <div class="absolute top-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              KMQ-CAM-001
            </div>
            <div class="absolute bottom-2 left-2 bg-dark/80 text-white text-xs px-2 py-1 rounded max-w-[60%] truncate">
              克拉玛依区世纪大道与友谊路口
            </div>
            <div class="absolute bottom-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              <i class="fa fa-exclamation-triangle text-warning mr-1"></i> 2起违法
            </div>
          </div>
          <div class="p-4">
            <div class="flex justify-between items-center mb-2">
              <h4 class="font-medium text-white">最近违法行为</h4>
              <button class="text-xs text-primary hover:text-primary/80">查看全部</button>
            </div>
            <div class="space-y-2">
              <div class="flex items-center gap-2 bg-gray-800 p-2 rounded">
                <img src="https://picsum.photos/id/1071/100/100" alt="违法截图" class="w-12 h-12 object-cover rounded">
                <div>
                  <p class="text-white text-sm font-medium">新K·A12345</p>
                  <p class="text-gray-400 text-xs">闯红灯 · 10:23</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card bg-gray-900 p-0 overflow-hidden">
          <div class="relative">
            <img src="https://picsum.photos/id/1018/800/450" alt="监控画面" class="w-full h-48 object-cover">
            <div class="absolute top-2 left-2 bg-danger/80 text-white text-xs px-2 py-1 rounded">
              <i class="fa fa-circle mr-1 animate-pulse"></i> 直播中
            </div>
            <div class="absolute top-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              BJT-CAM-003
            </div>
            <div class="absolute bottom-2 left-2 bg-dark/80 text-white text-xs px-2 py-1 rounded max-w-[60%] truncate">
              白碱滩区和平路
            </div>
            <div class="absolute bottom-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              <i class="fa fa-exclamation-triangle text-warning mr-1"></i> 1起违法
            </div>
          </div>
          <div class="p-4">
            <div class="flex justify-between items-center mb-2">
              <h4 class="font-medium text-white">最近违法行为</h4>
              <button class="text-xs text-primary hover:text-primary/80">查看全部</button>
            </div>
            <div class="space-y-2">
              <div class="flex items-center gap-2 bg-gray-800 p-2 rounded">
                <img src="https://picsum.photos/id/1073/100/100" alt="违法截图" class="w-12 h-12 object-cover rounded">
                <div>
                  <p class="text-white text-sm font-medium">新K·C24681</p>
                  <p class="text-gray-400 text-xs">逆行 · 08:15</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card bg-gray-900 p-0 overflow-hidden">
          <div class="relative">
            <img src="https://picsum.photos/id/1019/800/450" alt="监控画面" class="w-full h-48 object-cover">
            <div class="absolute top-2 left-2 bg-gray-500/80 text-white text-xs px-2 py-1 rounded">
              <i class="fa fa-power-off mr-1"></i> 离线
            </div>
            <div class="absolute top-2 right-2 bg-dark/80 text-white text-xs px-2 py-1 rounded">
              DSZ-CAM-004
            </div>
            <div class="absolute bottom-2 left-2 bg-dark/80 text-white text-xs px-2 py-1 rounded max-w-[60%] truncate">
              独山子区石化大道
            </div>
          </div>
          <div class="p-4">
            <div class="flex justify-between items-center mb-2">
              <h4 class="font-medium text-white">离线设备</h4>
              <p class="text-xs text-gray-400">最后在线: 2小时前</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 这个页面目前是纯静态展示，没有特定的脚本逻辑
</script>
