<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">线上执法流程</h2>
      <p class="text-gray-600">模拟交通违法案件的立案、审核与处置全流程</p>
    </div>

    <div class="card">
      <h3 class="font-bold text-gray-800 mb-2">案件处理: 新K·A12345 闯红灯</h3>
      <p class="text-sm text-gray-500 mb-6">案件编号: KLMY-20250609-001</p>

      <div class="flex mb-8">
        <div class="step flex-1" :class="{ 'step-active': currentStep >= 1 }">
          <div class="step-dot" :class="{ 'step-dot-active': currentStep >= 1 }"><i class="fa fa-gavel"></i></div>
          <span class="ml-2">案件立案</span>
          <div class="step-line" :class="{ 'step-line-active': currentStep >= 1 }"></div>
        </div>
        <div class="step flex-1" :class="{ 'step-active': currentStep >= 2 }">
          <div class="step-dot" :class="{ 'step-dot-active': currentStep >= 2 }">2</div>
          <span class="ml-2">AI审查</span>
          <div class="step-line" :class="{ 'step-line-active': currentStep >= 2 }"></div>
        </div>
        <div class="step flex-1" :class="{ 'step-active': currentStep >= 3 }">
          <div class="step-dot" :class="{ 'step-dot-active': currentStep >= 3 }">3</div>
          <span class="ml-2">处罚建议</span>
          <div class="step-line" :class="{ 'step-line-active': currentStep >= 3 }"></div>
        </div>
        <div class="step flex-1" :class="{ 'step-active': currentStep >= 4 }">
          <div class="step-dot" :class="{ 'step-dot-active': currentStep >= 4 }">4</div>
          <span class="ml-2">线上审批</span>
        </div>
      </div>

      <div v-if="currentStep === 1">
        <h4 class="font-bold text-lg text-gray-700 mb-4">1. 案件立案</h4>
        <!-- 表单内容 -->
        <form class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div><label class="block text-sm font-medium text-gray-700">车牌号码</label><input type="text" value="新K·A12345" class="input mt-1 bg-gray-100" readonly></div>
            <div><label class="block text-sm font-medium text-gray-700">违法类型</label><input type="text" value="闯红灯" class="input mt-1 bg-gray-100" readonly></div>
            <div><label class="block text-sm font-medium text-gray-700">违法时间</label><input type="text" value="2025-06-09 10:23:45" class="input mt-1 bg-gray-100" readonly></div>
            <div><label class="block text-sm font-medium text-gray-700">违法地点</label><input type="text" value="克拉玛依区世纪大道..." class="input mt-1 bg-gray-100" readonly></div>
          </div>


          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">现场证据 (最多3张)</label>
            <div
              class="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md"
              @dragover.prevent
              @drop.prevent="handleDrop"
              :class="{ 'bg-blue-50': isDragging }"
            >
              <input type="file" multiple accept="image/*" @change="handleFileChange" ref="fileInput" hidden />
              <div class="space-y-1 text-center w-full">
                <i class="fa fa-cloud-upload mx-auto h-12 w-12 text-gray-400"></i>
                <p class="text-sm text-gray-600">拖拽文件或 <span class="text-primary cursor-pointer" @click="$refs.fileInput.click()">点击上传</span></p>
                <p class="text-xs text-gray-500">PNG, JPG, GIF up to 10MB</p>

                <!-- 已上传文件列表 -->
                <div v-if="uploadedFiles.length" class="mt-4 space-y-2">
                  <div v-for="(file, index) in uploadedFiles" :key="index" class="flex items-center justify-between text-sm">
                    <span class="truncate">{{ file.name }}</span>
                    <div class="flex items-center gap-2">
                      <progress :value="file.progress" max="100" class="w-20 h-2"></progress>
                      <button @click="removeFile(index)" class="text-red-500">×</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>


        </form>
        <div class="flex justify-center">
          <button @click="currentStep = 2" class="btn btn-primary mt-4">确定并进入AI审查</button>
        </div>
      </div>

      <div v-if="currentStep === 2">
        <h4 class="font-bold text-lg text-gray-700 mb-4">2. AI 合规性审查 (模拟)</h4>
        <!-- 审查结果与操作按钮 -->
        <div class="bg-gray-50 p-4 rounded-lg">
          <p class="text-gray-600">审查结果: <span class="font-bold text-success">照片清晰，车牌识别率 95%</span> (阈值>70%)</p>
          <p class="text-sm text-gray-500 mt-1">AI建议: 符合立案标准，可进入下一步。</p>
        </div>
        <div class="flex justify-end gap-3 mt-4">
          <button @click="currentStep = 1" class="btn btn-secondary">返回上一步</button>
          <button class="btn btn-secondary">保存草稿</button>
          <button @click="currentStep = 3" class="btn btn-primary">生成处罚建议</button>
        </div>
      </div>

      <div v-if="currentStep === 3">
        <h4 class="font-bold text-lg text-gray-700 mb-4">3. 处罚建议生成 (模拟)</h4>
        <!-- 建议输入与确认按钮 -->
        <div class="bg-blue-50 p-4 rounded-lg border border-blue-200">
          <p class="text-sm text-gray-600">匹配法规: 《道路交通安全法》第26条、第90条</p>
          <div class="mt-4">
            <label class="block text-sm font-medium text-gray-700">建议罚款金额 (元)</label>
            <input type="number" value="200" class="input mt-1">
            <p class="text-xs text-gray-500 mt-1">可调整范围: ±20%</p>
          </div>
          <div class="mt-4">
            <label class="block text-sm font-medium text-gray-700">建议记分</label>
            <input type="number" value="6" class="input mt-1 bg-gray-100" readonly>
          </div>
          <button @click="currentStep = 2" class="btn btn-secondary w-full">返回上一步</button>
          <button @click="currentStep = 4" class="btn btn-primary w-full mt-4">确认处罚建议</button>
        </div>
      </div>

      <div v-if="currentStep === 4">
        <h4 class="font-bold text-lg text-gray-700 mb-4">4. 线上审批流程 (模拟)</h4>
        <!-- 审批流程列表 -->
        <ul class="space-y-4">
          <li class="flex items-center gap-4">
            <span class="flex items-center justify-center w-10 h-10 rounded-full bg-green-100 text-green-600"><i class="fa fa-check"></i></span>
            <div>
              <p class="font-medium">执法员提交</p>
              <p class="text-sm text-gray-500">王警官 (001) 于 10:30 提交</p>
            </div>
            <span class="ml-auto badge bg-success text-white">已完成</span>
          </li>
          <li class="flex items-center gap-4 opacity-50">
            <span class="flex items-center justify-center w-10 h-10 rounded-full bg-gray-200"><i class="fa fa-user"></i></span>
            <div>
              <p class="font-medium">中队长审核</p>
              <p class="text-sm text-gray-500">待审核</p>
            </div>
            <span class="ml-auto badge bg-gray-400 text-white">待处理</span>
          </li>
          <li class="flex items-center gap-4 opacity-50">
            <span class="flex items-center justify-center w-10 h-10 rounded-full bg-gray-200"><i class="fa fa-user-secret"></i></span>
            <div>
              <p class="font-medium">大队长批准</p>
              <p class="text-sm text-gray-500">待审核</p>
            </div>
            <span class="ml-auto badge bg-gray-400 text-white">待处理</span>
          </li>
        </ul>
          <button @click="currentStep = 3" class="btn btn-secondary w-full">返回上一步</button>
          <button @click="currentStep = 1" class="btn btn-primary w-full mt-4">完成审批</button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const currentStep = ref(1) // 当前步骤：1-案件立案，2-AI审查，3-处罚建议，4-线上审批



// 文件上传相关
const uploadedFiles = ref([])
const isDragging = ref(false)
const fileInput = ref(null)

// 处理文件选择（点击上传）
function handleFileChange(event) {
  const files = Array.from(event.target.files).slice(0, 3 - uploadedFiles.value.length)
  addFiles(files)
}

// 处理拖拽上传
function handleDrop(event) {
  isDragging.value = false
  const files = Array.from(event.dataTransfer.files).slice(0, 3 - uploadedFiles.value.length)
  addFiles(files)
}

// 公共添加文件方法
function addFiles(newFiles) {
  newFiles.forEach(file => {
    uploadedFiles.value.push({
      name: file.name,
      progress: 0
    })

    // 模拟上传过程
    simulateUpload(uploadedFiles.value.length - 1)
  })
}

// 模拟上传（替换为你自己的 axios/fetch 请求）
function simulateUpload(index) {
  let progress = 0
  const interval = setInterval(() => {
    if (progress >= 100) {
      clearInterval(interval)
      return
    }
    progress += 5
    uploadedFiles.value[index].progress = progress
  }, 100)
}

// 删除文件
function removeFile(index) {
  uploadedFiles.value.splice(index, 1)
}


// 这个页面目前是纯静态展示，没有特定的脚本逻辑
</script>
