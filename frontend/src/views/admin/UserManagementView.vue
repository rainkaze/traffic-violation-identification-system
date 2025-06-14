<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">用户管理</h2>
      <p class="text-gray-600">管理系统用户和处理注册申请</p>
    </div>

    <div class="card">
      <div class="flex justify-between items-center mb-4">
        <div class="border-b border-gray-200">
          <nav class="-mb-px flex space-x-8">
            <button @click="activeTab = 'pending'" :class="[activeTab === 'pending' ? 'border-primary text-primary' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300']" class="whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
              待审批申请 <span v-if="pendingUsers.length > 0" class="ml-2 bg-warning text-white text-xs rounded-full px-2 py-0.5">{{ pendingUsers.length }}</span>
            </button>
            <button @click="activeTab = 'all'" :class="[activeTab === 'all' ? 'border-primary text-primary' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300']" class="whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
              所有用户
            </button>
          </nav>
        </div>
        <button @click="openCreateUserModal" class="btn btn-primary"><i class="fa fa-plus mr-1"></i> 新建用户</button>
      </div>


      <div v-if="activeTab === 'pending'">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50"><tr><th class="px-6 py-3 text-left">姓名</th><th class="px-6 py-3 text-left">邮箱</th><th class="px-6 py-3 text-left">申请等级</th><th class="px-6 py-3 text-left">申请时间</th><th class="px-6 py-3 text-right">操作</th></tr></thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="isLoading"><td colspan="5" class="text-center py-4">加载中...</td></tr>
            <tr v-else-if="pendingUsers.length === 0"><td colspan="5" class="text-center py-4 text-gray-500">暂无待审批的申请</td></tr>
            <tr v-for="user in pendingUsers" :key="user.userId">
              <td class="px-6 py-4">{{ user.fullName }}</td>
              <td class="px-6 py-4">{{ user.email }}</td>
              <td class="px-6 py-4">{{ user.rank }}</td>
              <td class="px-6 py-4">{{ new Date(user.createdAt).toLocaleString() }}</td>
              <td class="px-6 py-4 text-right space-x-2">
                <button @click="approveUser(user.userId)" class="btn btn-success text-xs">批准</button>
                <button @click="rejectUser(user.userId)" class="btn btn-danger text-xs">拒绝</button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === 'all'">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50"><tr><th class="px-6 py-3 text-left">用户</th><th class="px-6 py-3 text-left">等级</th><th class="px-6 py-3 text-left">状态</th><th class="px-6 py-3 text-right">操作</th></tr></thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="isLoading"><td colspan="4" class="text-center py-4">加载中...</td></tr>
            <tr v-for="user in allUsers" :key="user.userId">
              <td class="px-6 py-4">
                <div class="font-medium">{{ user.fullName }}</div>
                <div class="text-sm text-gray-500">{{ user.email }}</div>
              </td>
              <td class="px-6 py-4">{{ user.rank }}</td>
              <td class="px-6 py-4">
                    <span :class="{
                        'bg-green-100 text-green-800': user.registrationStatus === 'APPROVED',
                        'bg-yellow-100 text-yellow-800': user.registrationStatus === 'PENDING',
                        'bg-red-100 text-red-800': user.registrationStatus === 'REJECTED'
                    }" class="badge">{{ user.registrationStatus }}</span>
              </td>
              <td class="px-6 py-4 text-right"><button class="text-primary mr-3">编辑</button></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import apiClient from '@/services/api';

const activeTab = ref('pending');
const users = ref([]);
const isLoading = ref(true);

const pendingUsers = computed(() => users.value.filter(u => u.registrationStatus === 'PENDING'));
const allUsers = computed(() => users.value);

const fetchUsers = async () => {
  isLoading.value = true;
  try {
    const response = await apiClient.get('/admin/users');
    users.value = response.data;
  } catch (error) {
    console.error("无法加载用户列表", error);
    alert('无法加载用户列表');
  } finally {
    isLoading.value = false;
  }
};

const approveUser = async (userId) => {
  if (!confirm('确定要批准这个用户的注册申请吗？')) return;
  try {
    await apiClient.post(`/admin/users/${userId}/approve`);
    alert('用户已批准，通知邮件已发送。');
    await fetchUsers();
  } catch (error) {
    console.error("批准用户失败", error);
    alert('操作失败');
  }
};

const rejectUser = async (userId) => {
  if (!confirm('确定要拒绝这个用户的注册申请吗？')) return;
  try {
    await apiClient.post(`/admin/users/${userId}/reject`);
    alert('用户申请已拒绝。');
    await fetchUsers();
  } catch (error) {
    console.error("拒绝用户失败", error);
    alert('操作失败');
  }
};

const openCreateUserModal = () => {
  // In a real app, this would open a modal form.
  // For this example, we'll just log to console.
  alert("（模拟）打开新建用户模态框。");
  console.log("Admin wants to create a new user.");
}

onMounted(fetchUsers);
</script>
