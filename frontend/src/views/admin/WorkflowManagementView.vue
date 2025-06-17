<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">工作流管理</h2>
      <p class="text-gray-600">创建和管理自动化业务流程</p>
    </div>

    <div class="card max-w-2xl mx-auto">
      <h3 class="text-xl font-bold mb-6">新建工作流</h3>
      <form @submit.prevent="createWorkflow">
        <div class="space-y-4">
          <div>
            <label for="workflowName" class="block text-sm font-medium text-gray-700">工作流名称</label>
            <input v-model="form.workflowName" type="text" id="workflowName" class="input mt-1 w-full" placeholder="例如：严重违法处理流程" required>
          </div>
          <div>
            <label for="description" class="block text-sm font-medium text-gray-700">描述</label>
            <textarea v-model="form.description" id="description" rows="3" class="input mt-1 w-full" placeholder="简要描述此工作流的用途和适用场景"></textarea>
          </div>
          <div class="flex items-center">
            <input v-model="form.isActive" type="checkbox" id="isActive" class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary">
            <label for="isActive" class="ml-2 block text-sm text-gray-900">立即激活此工作流</label>
          </div>
        </div>

        <div v-if="error" class="text-red-600 text-sm mt-4">{{ error }}</div>
        <div v-if="success" class="text-green-600 text-sm mt-4">{{ success }}</div>

        <div class="mt-6 flex justify-end">
          <button type="submit" :disabled="isLoading" class="btn btn-primary">
            <span v-if="isLoading">创建中...</span>
            <span v-else>创建工作流</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import apiClient from '@/services/api';

const form = reactive({
  workflowName: '',
  description: '',
  isActive: false,
});

const isLoading = ref(false);
const error = ref('');
const success = ref('');

const createWorkflow = async () => {
  isLoading.value = true;
  error.value = '';
  success.value = '';

  try {
    const response = await apiClient.post('/admin/workflows', form);
    success.value = `工作流 "${response.data.workflowName}" 已成功创建！`;
    // 清空表单以便创建下一个
    form.workflowName = '';
    form.description = '';
    form.isActive = false;
  } catch (err) {
    error.value = err.response?.data?.message || '创建工作流失败。';
  } finally {
    isLoading.value = false;
  }
};
</script>
