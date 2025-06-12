<template>
  <main class="p-8">
    <h1 class="text-2xl font-bold mb-4">前后端交互测试</h1>

    <div class="space-y-4">
      <button
        @click="fetchHelloFromBackend"
        class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        从后端获取问候语
      </button>

      <div v-if="message" class="p-4 bg-green-100 border border-green-400 text-green-800 rounded-lg">
        <p class="font-semibold">成功从后端获取到消息：</p>
        <h2 class="text-xl">{{ message }}</h2>
      </div>

      <div v-if="error" class="p-4 bg-red-100 border border-red-400 text-red-800 rounded-lg">
        <p class="font-semibold">出错了：</p>
        <p>{{ error }}</p>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 创建一个响应式变量来存储从后端返回的消息
const message = ref('');
const error = ref('');

// 定义一个方法来调用后端 API
const fetchHelloFromBackend = async () => {
  try {
    // 清空上一次的状态
    message.value = '';
    error.value = '';

    // 发起 GET 请求到 Spring Boot 后端的 /hello 接口
    // 在开发环境下，必须使用完整的 URL
    const response = await axios.get('http://localhost:8080/hello');

    // 将返回的数据赋值给 message
    message.value = response.data;

  } catch (err) {
    // 如果请求失败（比如后端没启动，或者CORS配置错误），捕获错误
    console.error('API call failed:', err);
    error.value = '无法连接到后端服务。请确保后端正在运行，并且CORS已正确配置。';
  }
};
</script>
