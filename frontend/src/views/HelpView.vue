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

          <!-- 更新日志 -->
          <button class="sidebar-item w-full text-left"
                  :class="{ 'bg-primary/10 text-primary': activeTab === 'changelog' }"
                  @click="switchTab('changelog')">
            <i class="fa fa-newspaper-o w-5 text-center text-gray-500"></i>
            <span>更新日志</span>
          </button>
          <!-- 联系支持 -->
          <button class="sidebar-item w-full text-left"
                  :class="{ 'bg-primary/10 text-primary': activeTab === 'contact' }"
                  @click="switchTab('contact')">
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
                  <button class="btn btn-secondary" @click="goToMail">
                    <i class="fa fa-envelope mr-1"></i> 发送邮件
                  </button>
                  <button class="btn btn-primary" @click="openAiChat">
                    <span class="inline-flex items-center justify-center w-5 h-5 mr-1 bg-white text-primary rounded-full text-xs font-bold">AI</span>
                    AI智能客服
                  </button>


                  <!-- AI 客服对话框 -->
                  <div v-if="showAiChat" class="ai-chat-dialog card fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[60rem] p-6 z-50 shadow-xl rounded-lg bg-white border border-primary">
                    <h3 class="font-bold text-gray-800 mb-4">AI智能客服</h3>
                    <!-- 模型选择 -->
                    <div class="mb-4 flex items-center gap-4">
                      <label class="inline-flex items-center">
                        <input type="radio" v-model="aiModel" value="v3" class="form-radio h-4 w-4 text-primary">
                        <span class="ml-2">V3 模式</span>
                      </label>
                      <label class="inline-flex items-center">
                        <input type="radio" v-model="aiModel" value="r1" class="form-radio h-4 w-4 text-primary">
                        <span class="ml-2">R1 模式</span>
                      </label>
                    </div>

                    <!-- 显示推理内容（仅 R1） -->
                    <div v-if="aiModel === 'r1'" class="mb-4">
                      <label class="inline-flex items-center">
                        <input type="checkbox" v-model="showReasoning" class="form-checkbox h-4 w-4 text-primary">
                        <span class="ml-2">显示思考过程</span>
                      </label>
                    </div>
                    <div ref="chatContainer" class="messages h-96 overflow-y-auto rounded-md p-3 mb-4 bg-gray-50 border border-gray-200">
                      <div v-for="(msg, index) in chatMessages" :key="index" class="mb-3 flex" :class="{ 'justify-end': msg.isUser }">
                        <!-- AI 消息头像 -->
                        <div class="flex-shrink-0 w-8 h-8 rounded-full overflow-hidden mr-2" v-if="!msg.isUser">
                          <img src="https://i.postimg.cc/5tyYYZTp/image.jpg" alt="智能客服" class="w-full h-full object-cover">
                        </div>

                        <!-- 气泡消息 -->
                        <div
                          :class="['max-w-lg px-4 py-2 rounded-lg',msg.isUser ? 'bg-primary text-white rounded-tr-none' : 'bg-white text-gray-800 rounded-tl-none border border-gray-300']">
                          <strong :class="{'text-white': msg.isUser}" class="block mb-1">{{ msg.sender }}:</strong>
                          <!-- 这里仍然是 Markdown 渲染 -->
                          <div class="whitespace-pre-line" v-html="renderMarkdown(msg.text)"></div>
                        </div>

                        <!-- 用户头像 -->
                        <div class="flex-shrink-0 w-8 h-8 rounded-full overflow-hidden ml-2" v-if="msg.isUser">
                          <img src="https://i.postimg.cc/brk2YkWh/image.jpg" alt="用户头像" class="w-full h-full object-cover">
                        </div>
                      </div>
                    </div>
                    <div v-if="isAiTyping" class="text-gray-500 italic">AI 正在思考中...</div>


                    <div class="flex gap-2">
                      <input v-model="userInput" @keyup.enter="sendMessage" placeholder="输入问题..." class="flex-grow p-3 border border-gray-300 rounded text-base focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent" />
                      <button @click="sendMessage" class="btn btn-primary px-4 py-2 hover:bg-blue-600 transition-colors">
                        发送
                      </button>
                    </div>
                    <button @click="closeAiChat" class="mt-4 btn btn-secondary w-full py-2 rounded">关闭</button>
                  </div>
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

          <!-- 第一个：测试视频1 -->
          <div class="mb-6">
            <h4 class="text-gray-700 font-medium mb-2">测试视频1</h4>
            <div class="w-full max-w-lg mx-auto rounded shadow overflow-hidden aspect-video">
              <iframe
                class="w-full h-full"
                :src="`https://player.bilibili.com/player.html?bvid=${getBvid(testVideo1)}&high_quality=1&danmaku=0`"
                frameborder="0"
                allowfullscreen>
              </iframe>
            </div>
          </div>

          <!-- 第二个：测试视频2 -->
          <div>
            <h4 class="text-gray-700 font-medium mb-2">测试视频2</h4>
            <div class="w-full max-w-lg mx-auto rounded shadow overflow-hidden aspect-video">
              <iframe
                class="w-full h-full"
                :src="`https://player.bilibili.com/player.html?bvid=${getBvid(testVideo2)}&high_quality=1&danmaku=0`"
                frameborder="0"
                allowfullscreen>
              </iframe>
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

const activeTab = ref('faq');
const switchTab = (tabName) => {
  activeTab.value = tabName;
};
// 使用新的远程视频 URL
const testVideo1 = 'https://www.bilibili.com/video/BV1iTN4zcEQS/?share_source=copy_web&vd_source=b9141f24c7341116073bc1682ecd5e93';
const testVideo2 = 'https://www.bilibili.com/video/BV1iTN4zcEJX/?share_source=copy_web&vd_source=b9141f24c7341116073bc1682ecd5e93';
// 从完整 URL 提取 bvid
function getBvid(url) {
  const match = url.match(/BV[0-9A-Za-z]{10}/);
  return match ? match[0] : '';
}


const activeFaq = ref(null);
const faqs = ref([
  {
    question: '如何进行系统登录问题排查？',
    answer: `<p class="text-gray-600 mb-3">要进行系统登录问题排查，请按照以下步骤操作：</p>
               <ol class="list-decimal list-inside space-y-1 text-gray-600">
                   <li>确认您的用户名和密码是否正确</li>
                   <li>检查网络连接</li>
                   <li>如果忘记密码，请点击“登录页面”的“忘记密码”进行重置</li>
               </ol>`
  },
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
  {
    question: '如何处理违法记录？',
    answer: `<p class="text-gray-600 mb-3">要处理违法记录，请按照以下步骤操作：</p>
               <ol class="list-decimal list-inside space-y-1 text-gray-600">
                   <li>进入"违法处理"页面</li>
                   <li>找到需要处理的记录--通过搜索框搜索关键词可快速定位到相应的违法记录</li>
                   <li>点击右侧的"处理"按钮</li>
                   <li>点击左侧“详情”按钮即可查看违法详情</li>
               </ol>`
  },
  {
    question: '如何导出统计数据？',
    answer: `<p class="text-gray-600 mb-3">要导出统计数据，请按照以下步骤操作：</p>
               <ol class="list-decimal list-inside space-y-1 text-gray-600">
                   <li>进入"违法记录"页面</li>
                   <li>找到需要导出的记录--通过搜索框搜索关键词可快速定位到相应的违法记录</li>
                   <li>勾选左侧的按钮</li>
                   <li>点击右上角“导出数据”</li>
               </ol>`
  },
  {
    question: '如何申请权限变更？',
    answer: `<p class="text-gray-600 mb-3">要申请权限变更，请按照以下步骤操作：</p>
               <ol class="list-decimal list-inside space-y-1 text-gray-600">
                   <li>请联系您的上级主管或系统管理员</li>
                   <li>进入“系统设置”页面</li>
                   <li>进入“用户管理”下级页面</li>
                   <li>找到相应用户--可通过搜索框快速定位用户</li>
                   <li>点击右侧“编辑”按钮进行权限调整</li>
                   <li>也可通过“禁用”/“启用”按钮进行快捷操作</li>
               </ol>`
  },
]);

const goToMail = () => {
  window.open("https://wx.mail.qq.com/", "_blank");
};

const showAiChat = ref(false);
const userInput = ref('');
const chatMessages = ref([]);
const chatContainer = ref(null);

const openAiChat = () => {
  showAiChat.value = true;
  chatMessages.value = [
    { text: '你好，请问有什么可以帮助你的吗？', isUser: false, sender: '智能客服' },
  ];
  scrollToBottom();
};

const closeAiChat = () => {
  showAiChat.value = false;
  userInput.value = '';
};

const isAiTyping = ref(false);
const aiModel = ref('v3'); // 当前 AI 模型：'v3' 或 'r1'
const showReasoning = ref(false); // 是否显示推理过程（仅 r1 模式）
const sendMessage = async () => {
  const message = userInput.value.trim();
  if (!message) return;

  chatMessages.value.push({ text: message, isUser: true, sender: '用户' });
  userInput.value = '';
  isAiTyping.value = true;

  let systemPrompt = '';
  if (aiModel.value === 'v3') {
    systemPrompt = "你是一个智能客服助手，请回答用户的问题。请保持回答简洁、准确。";
  } else if (aiModel.value === 'r1') {
    systemPrompt = showReasoning.value
      ? "你是一个智能客服助手。请先逐步分析问题，详细展示你的推理过程，最后用【最终答案】分隔符给出简洁的答案。"
      : "你是一个智能客服助手，请回答用户的问题。请保持回答简洁、准确。";
  }

  try {
    const response = await fetch('https://api.deepseek.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer sk-c87575e411794a84b99dfbfd4d706052'
      },
      body: JSON.stringify({
        model: "deepseek-chat",
        messages: [
          { role: "system", content: systemPrompt },
          { role: "user", content: message }
        ],
        max_tokens: 500,
        temperature: 0.7,
        stream: true
      })
    });

    if (!response.ok) throw new Error('网络响应失败');

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let accumulatedText = '';

    chatMessages.value.push({ text: '', isUser: false, sender: '智能客服' });

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value);
      const lines = chunk.split('\n');

      for (const line of lines) {
        if (line.startsWith('data: ') && !line.includes('[DONE]')) {
          try {
            const json = JSON.parse(line.replace(/^data: /, ''));
            const content = json.choices[0]?.delta?.content || '';
            accumulatedText += content;

            // 分离显示：只显示【最终答案】之后的内容（当未勾选显示推理过程）
            if (!showReasoning.value && accumulatedText.includes("【最终答案】")) {
              const finalAnswer = accumulatedText.split("【最终答案】")[1].trim();
              chatMessages.value[chatMessages.value.length - 1].text = finalAnswer;
            } else {
              chatMessages.value[chatMessages.value.length - 1].text = accumulatedText;
            }

            setTimeout(scrollToBottom, 10);
          } catch (e) {
            console.error('解析错误:', e);
          }
        }
      }
    }
  } catch (error) {
    console.error('Error:', error);
    chatMessages.value.push({ text: '网络错误或模型不支持流式输出。', isUser: false, sender: '智能客服' });
  } finally {
    isAiTyping.value = false;
    setTimeout(scrollToBottom, 50);
  }
};
import { watch } from 'vue';

watch(aiModel, () => {
  chatMessages.value = [
    { text: `已切换至 ${aiModel.value === 'v3' ? 'V3 精简模式' : 'R1 推理模式'}`, isUser: false, sender: '系统' }
  ];
});


import markdownIt from 'markdown-it';
const md = markdownIt(); // 初始化 Markdown 解析器
function renderMarkdown(text) {
  return md.render(text || '');
}

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
};

const activeManualSection = ref(null);
const manualSections = ref([
  {
    title: "1. 登录与权限管理手册",
    content: `<ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>输入用户名和密码登录系统</li>
        <li><span class="mr-2 font-medium">b.</span>忘记密码可点击登录页“忘记密码”进行重置</li>
        <li><span class="mr-2 font-medium">c.</span>没有账号可点击登录页“注册账号”进行注册</li>
        <li><span class="mr-2 font-medium">d.</span>权限变更请联系上级主管或管理员，在【系统设置】→【用户管理】中调整</li>
      </ul>`
  },
  {
    title: "2. 设备管理手册",
    content: `<ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【设备管理】页面</li>
        <li><span class="mr-2 font-medium">b.</span>点击右上角“添加设备”按钮</li>
        <li><span class="mr-2 font-medium">c.</span>填写设备基本信息（名称、类型、位置等）</li>
        <li><span class="mr-2 font-medium">d.</span>输入设备 IP 地址及访问凭据</li>
        <li><span class="mr-2 font-medium">e.</span>点击“保存”完成添加</li>
      </ul>`
  },
  {
    title: "3. 违法记录处理手册",
    content: `<ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【违法记录】页面</li>
        <li><span class="mr-2 font-medium">b.</span>找到需要处理的记录</li>
        <li><span class="mr-2 font-medium">c.</span>点击右侧对应的处理按钮即可</li>
      </ul>`
  },
  {
    title: "4. 统计分析与报告导出手册",
    content: `<ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【统计分析】页面</li>
        <li><span class="mr-2 font-medium">b.</span>设置想要的筛选条件</li>
        <li><span class="mr-2 font-medium">c.</span>点击右上角“导出报告”按钮即可导出数据</li>
      </ul>`
  },
  {
    title: "5. 常见问题解答手册",
    content: `<ul class="list-[circle] list-inside text-gray-600 space-y-1 pl-4">
        <li><span class="mr-2 font-medium">a.</span>进入【帮助中心】页面</li>
        <li><span class="mr-2 font-medium">b.</span>点击【常见问题】</li>
        <li><span class="mr-2 font-medium">c.</span>可在其中查找解决方案</li>
        <li><span class="mr-2 font-medium">d.</span>未找到解决方案？点击下方“AI智能客服”进行问答</li>
      </ul>`
  }
]);

const toggleManualSection = (index) => {
  activeManualSection.value === index
    ? (activeManualSection.value = null)
    : (activeManualSection.value = index);
};


const contactSupport = () => {
  window.location.href = "mailto:support@traffic-system.com?subject=技术支持请求";
};

const toggleFaq = (index) => {
  activeFaq.value === index ? (activeFaq.value = null) : (activeFaq.value = index);
};
</script>

<style scoped>
.sidebar-item {
  @apply flex items-center gap-2 px-3 py-2 rounded text-sm font-medium text-[#333] transition-colors;
}

.sidebar-item:hover {
  @apply bg-gray-100;
}

.messages pre {
  background-color: #f1f1f1;
  padding: 0.5rem;
  border-radius: 0.25rem;
  overflow-x: auto;
}

.messages code {
  background-color: #eee;
  padding: 0.2em 0.4em;
  border-radius: 3px;
}
</style>
