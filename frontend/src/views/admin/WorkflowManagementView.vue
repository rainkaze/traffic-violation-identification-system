<template>
  <div class="p-4">
    <div class="card mb-6">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-xl font-bold text-gray-800">工作流列表</h3>
        <router-link to="/workflow-management/new" class="btn btn-primary">
          <i class="fa fa-plus mr-1"></i> 新建工作流
        </router-link>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left">名称</th>
            <th class="px-6 py-3 text-left">节点数</th>
            <th class="px-6 py-3 text-left">状态</th>
            <th class="px-6 py-3 text-left">创建人</th>
            <th class="px-6 py-3 text-right">操作</th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-if="loading"><td colspan="5" class="text-center py-4">加载中...</td></tr>
          <tr v-for="wf in workflows" :key="wf.workflowId">
            <td class="px-6 py-4 font-medium">{{ wf.workflowName }}</td>
            <td class="px-6 py-4">{{ wf.nodeCount }}</td>
            <td class="px-6 py-4">
              <button @click="toggleActivation(wf)" class="badge cursor-pointer" :class="wf.isActive ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'">
                <i :class="wf.isActive ? 'fa fa-check-circle' : 'fa fa-power-off'"></i>
                {{ wf.isActive ? '已激活' : '未激活' }}
              </button>
            </td>
            <td class="px-6 py-4">{{ wf.createdByFullName }}</td>
            <td class="px-6 py-4 text-right">
              <router-link :to="{ name: 'workflow-form-edit', params: { id: wf.workflowId } }"
                           class="text-primary hover:text-primary/80 mr-3">
                编辑
              </router-link>
              <button @click="deleteWorkflow(wf.workflowId)"
                      class="text-danger hover:text-danger/80">删除
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
// <script>部分的代码完全不用修改
import {ref, onMounted} from 'vue';
import apiClient from '@/services/api';

const loading = ref(true);
const workflows = ref([]);

const fetchWorkflows = async () => {
  loading.value = true;
  try {
    const response = await apiClient.get('/admin/workflows');
    workflows.value = response.data;
  } catch (e) {
    console.error("无法加载工作流列表:", e);
    alert('无法加载工作流列表');
  } finally {
    loading.value = false;
  }
};

const deleteWorkflow = async (id) => {
  if (!confirm('确定要删除这个工作流吗？此操作不可撤销。')) return;
  try {
    await apiClient.delete(`/admin/workflows/${id}`);
    alert('工作流已删除。');
    await fetchWorkflows();
  } catch (e) {
    console.error("删除工作流失败:", e);
    alert('删除失败');
  }
};

const toggleActivation = async (workflow) => {
  try {
    // 后端返回的是布尔值，直接赋值
    const response = await apiClient.post(`/admin/workflows/${workflow.workflowId}/toggle-activation`);
    workflow.isActive = response.data;
    alert('状态更新成功！');
  } catch (e) {
    console.error("更新状态失败:", e);
    alert('状态更新失败');
  }
};

onMounted(fetchWorkflows);
</script>
