<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">事故处置引导 (模拟功能)</h2>
      <p class="text-gray-600">模拟事故检测、策略生成和处置引导</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="card lg:col-span-2">
        <h3 class="font-bold text-gray-800 mb-4">克拉玛依市交通态势图</h3>
        <div class="relative w-full h-[600px] bg-gray-200 rounded-lg flex items-center justify-center overflow-hidden">
          <img src="/map.png" class="object-cover w-full h-full" alt="克拉玛依市交通态势图"/>
          <div class="absolute" style="top: 30%; left: 45%;">
            <div class="p-2 bg-white rounded-lg shadow-xl">
              <div class="flex items-center gap-2">
                <i class="fa fa-car text-danger text-2xl animate-pulse"></i>
                <div>
                  <p class="font-bold text-sm">疑似事故</p>
                  <p class="text-xs">世纪大道与和平路口</p>
                </div>
              </div>
            </div>
          </div>
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

<script setup>
// 这个页面目前是纯静态展示，没有特定的脚本逻辑
// 为了让图片显示，请确保在 `frontend/public` 目录下有名为 map.png 和 reli.jpg 的图片文件。
</script>
