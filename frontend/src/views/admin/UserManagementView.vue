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
      </div>

      <div v-if="activeTab === 'all'">
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left">用户</th>
              <th class="px-6 py-3 text-left">等级</th>
              <th class="px-6 py-3 text-left">辖区</th>
              <th class="px-6 py-3 text-left">状态</th>
              <th class="px-6 py-3 text-right">操作</th>
            </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="isLoading"><td colspan="5" class="text-center py-4">加载中...</td></tr>
            <tr v-for="user in allUsers" :key="user.userId">
              <td class="px-6 py-4">
                <div class="font-medium">{{ user.fullName }}</div>
                <div class="text-sm text-gray-500">{{ user.email }}</div>
              </td>
              <td class="px-6 py-4">{{ user.rank }}</td>
              <td class="px-6 py-4">
                  <span v-if="user.districts && user.districts.length" class="flex flex-wrap gap-1">
                    <span v-for="district in user.districts" :key="district" class="badge bg-blue-100 text-blue-800">
                      {{ district }}
                    </span>
                  </span>
                <span v-else class="text-gray-400">未分配</span>
              </td>
              <td class="px-6 py-4">
                  <span :class="{
                      'bg-green-100 text-green-800': user.registrationStatus === 'APPROVED',
                      'bg-yellow-100 text-yellow-800': user.registrationStatus === 'PENDING',
                      'bg-red-100 text-red-800': user.registrationStatus === 'REJECTED'
                  }" class="badge">{{ user.registrationStatus }}</span>
              </td>
              <td class="px-6 py-4 text-right whitespace-nowrap">
                <button @click="openDistrictModal(user)" class="text-blue-600 hover:text-blue-800 mr-3" v-if="user.rank !== '管理员'">辖区</button>
                <button @click="openEditUserModal(user)" class="text-primary hover:text-primary/80 mr-3">编辑</button>
                <button @click="handleDeleteUser(user.userId)" class="text-danger hover:text-danger/80">删除</button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <UserFormModal :show="showModal" :user="currentUserForEdit" @close="closeModal" @save="handleSave" />
    <UserDistrictModal :show="showDistrictModal" :user="currentUserForDistrict" :all-districts="districts" @close="closeDistrictModal" @save="handleSave" />
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import apiClient from '@/services/api';
import UserFormModal from '@/components/admin/UserFormModal.vue'; // 引入模态框组件
import UserDistrictModal from '@/components/admin/UserDistrictModal.vue';

const activeTab = ref('pending');
const users = ref([]);
const districts = ref([]); // 存储所有辖区列表
const isLoading = ref(true);

const showModal = ref(false);
const showDistrictModal = ref(false);
const currentUserForEdit = ref(null);
const currentUserForDistrict = ref(null);


const pendingUsers = computed(() => users.value.filter(u => u.registrationStatus === 'PENDING'));
// 过滤掉待审批的用户，使其只在“待审批”标签页显示
const allUsers = computed(() => users.value.filter(u => u.registrationStatus !== 'PENDING'));

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

const fetchDistricts = async () => {
  try {
    const response = await apiClient.get('/districts');
    districts.value = response.data;
  } catch (error) {
    console.error("无法加载辖区列表", error);
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
  currentUserForEdit.value = null; // 清空，表示是新建模式
  showModal.value = true;
};

const openEditUserModal = (user) => {
  currentUserForEdit.value = user; // 传入要编辑的用户数据
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};
const handleDeleteUser = async (userId) => {
  if (!confirm('确定要永久删除该用户吗？此操作不可撤销。')) return;
  try {
    await apiClient.delete(`/admin/users/${userId}`);
    alert('用户已删除。');
    await fetchUsers();
  } catch (error) {
    alert(error.response?.data?.message || '删除用户失败。');
    console.error("删除用户失败", error);
  }
};

const openDistrictModal = (user) => {
  currentUserForDistrict.value = user;
  showDistrictModal.value = true;
};

const closeDistrictModal = () => {
  showDistrictModal.value = false;
};

const handleSave = () => {
  // 无论是编辑用户还是分配辖区，都刷新用户列表
  fetchUsers();
  closeModal();
  closeDistrictModal();
};

onMounted(() => {
  fetchUsers();
  fetchDistricts(); // 页面加载时获取辖区列表
});
</script>
