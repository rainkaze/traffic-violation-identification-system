<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">帮助中心</h2>
      <p class="text-gray-600">系统使用指南与技术支持</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-6">
      <div class="card lg:col-span-1 p-0">
        <div class="space-y-1 p-3">
          <button class="sidebar-item w-full text-left" :class="{ 'bg-primary/10 text-primary': activeTab === 'faq' }"
                  @click="switchTab('faq')">
            <i class="fa fa-question-circle w-5 text-center text-primary"></i>
            <span>常见问题</span>
          </button>


          <button class="sidebar-item w-full text-left" :class="{ 'bg-primary/10 text-primary': activeTab === 'manual' }"
                  @click="switchTab('manual')">
            <i class="fa fa-book w-5 text-center text-gray-500"></i>
            <span>使用手册</span>
          </button>

          <button class="sidebar-item w-full text-left" :class="{ 'bg-primary/10 text-primary': activeTab === 'video' }"
                  @click="switchTab('video')">
            <i class="fa fa-video-camera w-5 text-center text-gray-500"></i>
            <span>视频教程</span>
          </button>

          <!-- 其它按钮同理 -->
          <!-- 更新日志 -->
          <button class="sidebar-item w-full text-left"
                  :class="{ 'bg-primary/10 text-primary': activeTab === 'changelog' }"
                  @click="switchTab('changelog')"> <!-- ✅ 改成函数调用 -->
            <i class="fa fa-newspaper-o w-5 text-center text-gray-500"></i>
            <span>更新日志</span>
          </button>

          <!-- 联系支持 -->
          <button class="sidebar-item w-full text-left"
                  :class="{ 'bg-primary/10 text-primary': activeTab === 'contact' }"
                  @click="switchTab('contact')"> <!-- ✅ 改成函数调用 -->
            <i class="fa fa-envelope w-5 text-center text-gray-500"></i>
            <span>联系支持</span>
          </button>


        </div>
        <div class="mt-8 pt-4 border-t border-gray-200 p-4">
          <h4 class="font-medium text-gray-800 mb-3">支持服务</h4>
          <div class="space-y-3">
            <div class="flex items-start gap-2">
              <i class="fa fa-phone text-primary mt-1"></i>
              <div>
                <p class="font-medium">技术支持热线</p>
                <p class="text-sm text-gray-600">400-123-4567</p>
              </div>
            </div>
            <div class="flex items-start gap-2">
              <i class="fa fa-envelope-o text-primary mt-1"></i>
              <div>
                <p class="font-medium">邮箱支持</p>
                <p class="text-sm text-gray-600">support@traffic-system.com</p>
              </div>
            </div>
            <div class="flex items-start gap-2">
              <i class="fa fa-clock-o text-primary mt-1"></i>
              <div>
                <p class="font-medium">服务时间</p>
                <p class="text-sm text-gray-600">工作日 8:30-18:00</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card lg:col-span-3">
        <div v-if="activeTab === 'faq'">
          <!-- 原始 FAQ 内容 -->
          <h3 class="font-bold text-gray-800 mb-6">常见问题解答</h3>
          <div class="space-y-4">
            <div v-for="(faq, index) in faqs" :key="index" class="border border-gray-200 rounded-lg overflow-hidden">
              <button @click="toggleFaq(index)" class="flex justify-between items-center w-full p-4 bg-gray-50 hover:bg-gray-100">
                <span class="font-medium text-gray-800">{{ faq.question }}</span>
                <i class="fa text-gray-500 transition-transform" :class="activeFaq === index ? 'fa-angle-up' : 'fa-angle-down'"></i>
              </button>
              <div v-if="activeFaq === index" class="p-4 border-t border-gray-200">
                <div v-html="faq.answer"></div>
              </div>
            </div>
          </div>
          <div class="mt-8 pt-6 border-t border-gray-200">
            <h4 class="font-bold text-gray-800 mb-4">无法找到解决方案？</h4>
            <div class="bg-primary/10 border border-primary/30 rounded-lg p-4">
              <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                  <h5 class="font-medium text-gray-800">联系技术支持团队</h5>
                  <p class="text-sm text-gray-600">我们的专业团队将及时为您解决问题</p>
                </div>
                <div class="flex gap-3">
                  <button class="btn btn-secondary">
                    <i class="fa fa-envelope mr-1"></i> 发送邮件
                  </button>
                  <button class="btn btn-primary">
                    <i class="fa fa-phone mr-1"></i> 在线客服
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'manual'">
          <h3 class="font-bold text-gray-800 mb-6">使用手册</h3>
          <div class="space-y-4">
            <div v-for="(section, index) in manualSections" :key="index">
              <button
                @click="toggleManualSection(index)"
                class="flex justify-between items-center w-full p-4 bg-gray-50 hover:bg-gray-100 rounded-t-lg"
              >
                <span class="font-medium text-gray-800">{{ section.title }}</span>
                <i class="fa text-gray-500 transition-transform" :class="activeManualSection === index ? 'fa-angle-up' : 'fa-angle-down'"></i>
              </button>
              <div v-show="activeManualSection === index" class="p-4 border border-gray-200 rounded-b-lg bg-white">
                <div v-html="section.content"></div>
              </div>
            </div>
          </div>
        </div>



        <div v-else-if="activeTab === 'video'" class="p-4">
          <h3 class="font-bold text-gray-800 mb-4">视频教程</h3>
          <p class="text-gray-600 mb-4">请选择本地视频文件进行上传或预览（可选择多个）：</p>

          <!-- 多文件上传控件 -->
          <input type="file" accept="video/*" multiple @change="handleMultipleVideoUpload" class="mb-4" />

          <!-- 视频预览区域 -->
          <div class="space-y-6 mt-6">
            <div v-for="(url, index) in videoPreviewUrls" :key="index" class="relative group">
              <video :src="url" controls class="w-full max-w-lg mx-auto rounded shadow">
                您的浏览器不支持视频播放。
              </video>
              <button
                @click="removeVideo(index)"
                class="absolute top-2 right-2 bg-red-500 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                title="移除视频"
              >
                <i class="fa fa-trash"></i>
              </button>
            </div>
          </div>
        </div>


        <div v-else-if="activeTab === 'changelog'" class="p-4">
          <h3 class="font-bold text-gray-800 mb-4">系统更新日志</h3>
          <p class="text-gray-600">版本更新信息如下：</p>
          <div class="space-y-4 mt-4">
            <div class="border-l-4 border-primary pl-4 py-1">
              <div class="flex justify-between items-start">
                <h4 class="font-medium text-gray-800">版本 2.1.0</h4>
                <span class="text-sm text-gray-500">2025年5月15日</span>
              </div>
              <ul class="mt-2 space-y-1 text-sm text-gray-600">
                <li class="flex items-start gap-2">
                  <i class="fa fa-plus text-success mt-0.5"></i>
                  <span>新增实时监控多画面查看功能</span>
                </li>
                <li class="flex items-start gap-2">
                  <i class="fa fa-check text-success mt-0.5"></i>
                  <span>优化违法记录处理流程</span>
                </li>
                <li class="flex items-start gap-2">
                  <i class="fa fa-bug text-danger mt-0.5"></i>
                  <span>修复设备状态显示不准确的问题</span>
                </li>
              </ul>
            </div>
            <div class="border-l-4 border-secondary pl-4 py-1">
              <div class="flex justify-between items-start">
                <h4 class="font-medium text-gray-800">版本 2.0.3</h4>
                <span class="text-sm text-gray-500">2025年4月28日</span>
              </div>
              <ul class="mt-2 space-y-1 text-sm text-gray-600">
                <li class="flex items-start gap-2">
                  <i class="fa fa-plus text-success mt-0.5"></i>
                  <span>新增统计分析导出功能</span>
                </li>
                <li class="flex items-start gap-2">
                  <i class="fa fa-check text-success mt-0.5"></i>
                  <span>优化系统设置页面布局</span>
                </li>
              </ul>
            </div>
          </div>
        </div>


        <div v-else-if="activeTab === 'contact'" class="p-4">
          <h3 class="font-bold text-gray-800 mb-4">联系支持</h3>
          <p class="text-gray-600">如有技术问题，请通过以下方式联系我们：</p>
          <ul class="mt-4 space-y-2 text-gray-600">
            <li><strong>电话：</strong>400-123-4567</li>
            <li><strong>邮箱：</strong>support@traffic-system.com</li>
            <li><strong>服务时间：</strong>工作日 8:30-18:00</li>
          </ul>
          <button class="btn btn-primary mt-4" @click="contactSupport">发送邮件咨询</button>
        </div>
      </div>

    </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
const activeTab = ref('faq'); // 默认显示常见问题
const switchTab = (tabName) => {
  activeTab.value = tabName;
};
const activeFaq = ref(0);
const faqs = ref([
  {
    question: '如何添加新的监控设备？',
    answer: `<p class="text-gray-600 mb-3">要添加新的监控设备，请按照以下步骤操作：</p>
               <ol class="list-decimal list-inside space-y-1 text-gray-600">
                   <li>进入"设备管理"页面</li>
                   <li>点击右上角的"添加设备"按钮</li>
                   <li>填写设备基本信息（名称、类型、位置等）</li>
                   <li>输入设备的IP地址和访问凭据</li>
                   <li>点击"保存"完成添加</li>
               </ol>
               <p class="text-gray-600 mt-3">添加后，设备将出现在设备列表中，系统将自动尝试连接设备。</p>`
  },
  { question: '如何处理违法记录？', answer: '<p>在“违法记录”页面，找到需要处理的记录，点击右侧的“处理”按钮，即可进入处理流程。</p>'},
  { question: '如何导出统计数据？', answer: '<p>在“统计分析”页面，设置好您需要的筛选条件后，点击右上角的“导出报告”按钮即可。</p>'},
  { question: '系统登录问题排查', answer: '<p>请确认您的用户名和密码是否正确，并检查网络连接。如果忘记密码，请点击登录页面的“忘记密码”链接进行重置。</p>'},
  { question: '如何申请权限变更？', answer: '<p>请联系您的上级主管或系统管理员，在“系统设置”的“用户管理”中进行权限调整。</p>'},
]);
const activeManualSection = ref(0); // 默认打开第一个章节
const toggleManualSection = (index) => {
  if (activeManualSection.value === index) {
    activeManualSection.value = null; // 如果已经打开则关闭
  } else {
    activeManualSection.value = index; // 打开指定章节
  }
};
const manualSections = ref([
  {
    title: "1. 登录与权限管理手册",
    content: `      <ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>输入用户名和密码登录系统</li>
        <li><span class="mr-2 font-medium">b.</span>忘记密码可点击登录页“忘记密码”进行重置</li>
        <li><span class="mr-2 font-medium">c.</span>没有账号可点击登录页“注册账号”进行注册</li>
        <li><span class="mr-2 font-medium">d.</span>权限变更请联系上级主管或管理员，在【系统设置】→【用户管理】中调整</li>
      </ul>
    `
  },
  {
    title: "2. 设备管理手册",
    content: `      <ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【设备管理】页面</li>
        <li><span class="mr-2 font-medium">b.</span>点击右上角“添加设备”按钮</li>
        <li><span class="mr-2 font-medium">c.</span>填写设备基本信息（名称、类型、位置等）</li>
        <li><span class="mr-2 font-medium">d.</span>输入设备 IP 地址及访问凭据</li>
        <li><span class="mr-2 font-medium">e.</span>点击“保存”完成添加</li>
      </ul>
    `
  },
  {
    title: "3. 违法记录处理手册",
    //content: `<p class="text-gray-600">进入【违法记录】页面，找到需要处理的记录，点击右侧“处理”按钮即可。</p>`
    content: `      <ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【违法记录】页面</li>
        <li><span class="mr-2 font-medium">b.</span>找到需要处理的记录</li>
        <li><span class="mr-2 font-medium">c.</span>点击右侧对应的处理按钮即可</li>
      </ul>
    `

  },
  {
    title: "4. 统计分析与报告导出手册",
    //content: `<p class="text-gray-600">在【统计分析】页面设置筛选条件后，点击右上角“导出报告”按钮即可导出数据。</p>`
    content: `      <ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【统计分析】页面</li>
        <li><span class="mr-2 font-medium">b.</span>设置想要的筛选条件</li>
        <li><span class="mr-2 font-medium">c.</span>点击右上角“导出报告”按钮即可导出数据</li>
      </ul>
    `

  },
  {
    title: "5. 常见问题解答手册",
   // content: `<p class="text-gray-600">可在【帮助中心】→【常见问题】中查找解决方案。</p>`
    content: `      <ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【帮助中心】页面</li>
        <li><span class="mr-2 font-medium">b.</span>点击【常见问题】</li>
        <li><span class="mr-2 font-medium">c.</span>可在其中查找解决方案</li>
      </ul>
    `

  }
]);


const selectedVideos = ref([]); // 存储多个视频文件
const videoPreviewUrls = ref([]); // 存储多个视频预览链接
const contactSupport = () => {
  window.location.href = "mailto:support@traffic-system.com?subject=技术支持请求";
};
const handleMultipleVideoUpload = (event) => {
  const files = Array.from(event.target.files);
  if (!files.length) return;

  const validVideos = files.filter(file => file.type.startsWith('video/'));

  if (validVideos.length === 0) {
    alert('请选择有效的视频文件');
    return;
  }

  selectedVideos.value = [...selectedVideos.value, ...validVideos];

  // 生成预览链接
  const urls = validVideos.map(file => URL.createObjectURL(file));
  videoPreviewUrls.value = [...videoPreviewUrls.value, ...urls];
};
const removeVideo = (index) => {
  // 移除对象 URL 避免内存泄漏
  URL.revokeObjectURL(videoPreviewUrls.value[index]);

  // 删除数组中的对应项
  videoPreviewUrls.value.splice(index, 1);
  selectedVideos.value.splice(index, 1);
};

//切换FAQ的方法
const toggleFaq = (index) => {
  if (activeFaq.value === index) {
    activeFaq.value = null; // Close if already open
  } else {
    activeFaq.value = index; // Open the clicked one
  }
};

</script>
