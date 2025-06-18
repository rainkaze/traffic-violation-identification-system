<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">{{ pageTitle }}</h2>
      <p class="text-gray-600">{{ pageDescription }}</p>
    </div>

    <div v-if="pageLoading" class="text-center p-10">
      <p>正在加载工作流数据...</p>
    </div>
    <form v-else @submit.prevent="submitWorkflow" class="card max-w-4xl mx-auto space-y-8">
      <section>
        <h3 class="font-semibold text-lg border-b pb-2 mb-4">1. 基本信息</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">工作流名称*</label>
            <input v-model="form.workflowName" type="text" class="input mt-1 w-full" required>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">描述</label>
            <textarea v-model="form.description" rows="3" class="input mt-1 w-full"></textarea>
          </div>
        </div>
      </section>

      <section>
        <h3 class="font-semibold text-lg border-b pb-2 mb-4">2. 触发规则</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">适用辖区 (可选)</label>
            <select v-model="form.trigger.districtId" class="input mt-1 w-full">
              <option :value="null">所有辖区</option>
              <option v-for="d in districts" :key="d.districtId" :value="d.districtId">{{ d.districtName }}</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">违法类型 (可选)</label>
            <select v-model="form.trigger.ruleId" class="input mt-1 w-full">
              <option :value="null">所有类型</option>
              <option v-for="r in trafficRules" :key="r.ruleId" :value="r.ruleId">{{ r.violationType }}</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">扣分区间 (可选)</label>
            <div class="flex items-center gap-2 mt-1">
              <input v-model.number="form.trigger.minDemeritPoints" type="number" class="input w-full" placeholder="最小扣分">
              <span>-</span>
              <input v-model.number="form.trigger.maxDemeritPoints" type="number" class="input w-full" placeholder="最大扣分">
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">优先级*</label>
            <input v-model.number="form.trigger.priority" type="number" class="input mt-1 w-full" required>
          </div>
        </div>
      </section>

      <section>
        <h3 class="font-semibold text-lg border-b pb-2 mb-4">3. 流程节点</h3>
        <div class="space-y-4">
          <div v-for="(node, index) in form.nodes" :key="index" class="p-4 border rounded-lg space-y-3 bg-gray-50 relative">
            <button @click="removeNode(index)" type="button" class="absolute top-2 right-2 text-gray-400 hover:text-danger text-lg font-bold">&times;</button>
            <h4 class="font-bold text-gray-700">节点 {{ index + 1 }}</h4>
            <div>
              <label class="block text-sm font-medium text-gray-700">节点名称*</label>
              <input v-model="node.nodeName" type="text" class="input mt-1 w-full" required>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">完成规则*</label>
              <select v-model="node.completionRule" class="input mt-1 w-full">
                <option value="ANY_ASSIGNEE">或签 (任一处理即可)</option>
                <option value="ALL_ASSIGNEES">会签 (必须全部处理)</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">指派类型*</label>
              <select v-model="node.assignmentType" class="input mt-1 w-full">
                <option value="DYNAMIC_ROLE_IN_DISTRICT">按角色动态分配</option>
                <option value="STATIC_USER_LIST">静态指定用户</option>
              </select>
            </div>
            <div v-if="node.assignmentType === 'DYNAMIC_ROLE_IN_DISTRICT'">
              <label class="block text-sm font-medium text-gray-700">指定角色*</label>
              <select v-model="node.assignedRank" class="input mt-1 w-full">
                <option>警员</option>
                <option>中队长</option>
                <option>大队长</option>
              </select>
            </div>
            <div v-if="node.assignmentType === 'STATIC_USER_LIST'">
              <label class="block text-sm font-medium text-gray-700">指定用户*</label>
              <p class="text-xs text-gray-500">用户列表根据触发规则中的"适用辖区"动态筛选，若未选辖区则显示所有用户。</p>
              <input type="text" v-model="searchTerms[index]" placeholder="搜索姓名/职位..." class="input mt-1 w-full mb-2">
              <div class="max-h-40 overflow-y-auto border rounded-md p-2 space-y-1 bg-white">
                <label v-for="user in filteredUsersForNode(index)" :key="user.userId" class="flex items-center p-1 rounded hover:bg-blue-50 cursor-pointer">
                  <input type="checkbox" :value="user.userId" v-model="node.staticUserIds" class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary">
                  <span class="ml-3 text-sm text-gray-800">{{ user.fullName }} ({{user.rank}}) - [{{user.districts.join(', ')}}]</span>
                </label>
                <p v-if="!filteredUsersForNode(index).length" class="text-sm text-gray-500 text-center p-2">无匹配用户</p>
              </div>
            </div>
          </div>
          <button @click="addNode" type="button" class="btn btn-secondary w-full"><i class="fa fa-plus mr-1"></i> 添加节点</button>
        </div>
      </section>

      <div class="mt-8 pt-6 border-t flex items-center justify-between">
        <div class="flex items-center">
          <input v-model="form.isActive" type="checkbox" id="isActive" class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary">
          <label for="isActive" class="ml-2 block text-sm text-gray-900">
            {{ isEditMode ? '保持激活状态' : '创建后立即激活' }}
          </label>
        </div>
        <div class="flex gap-3">
          <router-link to="/workflow-management" class="btn btn-secondary">取消</router-link>
          <button type="submit" :disabled="isLoading" class="btn btn-primary">
            {{ isLoading ? '保存中...' : (isEditMode ? '更新工作流' : '确认并保存') }}
          </button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import apiClient from '@/services/api';

const route = useRoute();
const router = useRouter();

const isLoading = ref(false);
const pageLoading = ref(false);
const districts = ref([]);
const trafficRules = ref([]);
const allAssignableUsers = ref([]);
const searchTerms = reactive({});

const isEditMode = computed(() => !!route.params.id);
const workflowId = computed(() => route.params.id);

const pageTitle = computed(() => isEditMode.value ? '编辑工作流' : '新建工作流');
const pageDescription = computed(() => isEditMode.value ? '修改工作流的触发器、节点和负责人。' : '定义一个完整的工作流，包括触发器、节点和负责人。');

const createDefaultForm = () => ({
  workflowName: '',
  description: '',
  isActive: true,
  trigger: {
    districtId: null,
    ruleId: null,
    minDemeritPoints: null,
    maxDemeritPoints: null,
    priority: 0,
  },
  nodes: [],
});

const form = reactive(createDefaultForm());

const filteredUsersForNode = (nodeIndex) => {
  const searchTerm = (searchTerms[nodeIndex] || '').toLowerCase();
  if (!searchTerm) return allAssignableUsers.value;
  return allAssignableUsers.value.filter(user =>
    user.fullName.toLowerCase().includes(searchTerm) ||
    user.rank.toLowerCase().includes(searchTerm)
  );
};

// **关键修复点 1**: 修正 watch 逻辑
watch(() => form.trigger.districtId, (newDistrictId) => {
  // 当辖区变化时，只重新获取可供选择的用户列表
  // 不要清空用户已经做出的选择
  fetchAssignableUsers(newDistrictId);
}, { deep: true });

const addNode = () => {
  const newNodeIndex = form.nodes.length;
  form.nodes.push({
    nodeName: `审批节点 ${newNodeIndex + 1}`,
    stepOrder: newNodeIndex + 1,
    completionRule: 'ANY_ASSIGNEE',
    assignmentType: 'DYNAMIC_ROLE_IN_DISTRICT',
    assignedRank: '警员',
    staticUserIds: [],
  });
  searchTerms[newNodeIndex] = '';
};

const removeNode = (index) => {
  form.nodes.splice(index, 1);
  delete searchTerms[index];
  form.nodes.forEach((node, i) => node.stepOrder = i + 1);
};

const fetchDropdownData = async () => {
  try {
    const [districtsRes, rulesRes] = await Promise.all([
      apiClient.get('/districts'),
      apiClient.get('/rules'),
    ]);
    districts.value = districtsRes.data;
    trafficRules.value = rulesRes.data;
  } catch (e) { console.error("加载下拉框数据失败:", e); }
};

const fetchAssignableUsers = async (districtId) => {
  try {
    const params = districtId ? { districtId } : {};
    const response = await apiClient.get('/admin/users/for-assignment', { params });
    allAssignableUsers.value = response.data;
  } catch(e) { console.error("加载可指派用户失败:", e); }
};

const loadWorkflowForEdit = async () => {
  if (!isEditMode.value) return;
  pageLoading.value = true;
  try {
    const response = await apiClient.get(`/admin/workflows/${workflowId.value}`);
    const data = response.data;

    if (data.nodes && Array.isArray(data.nodes)) {
      data.nodes.forEach(node => {
        if (!Array.isArray(node.staticUserIds)) {
          node.staticUserIds = [];
        }
      });
    }

    form.workflowName = data.workflowName;
    form.description = data.description;
    form.isActive = data.isActive;
    form.trigger = data.trigger || createDefaultForm().trigger;
    form.nodes = data.nodes || [];

    form.nodes.forEach((_, index) => searchTerms[index] = '');

  } catch (e) {
    console.error("加载工作流详情失败:", e);
    alert('无法加载工作流详情，将返回列表页。');
    router.push('/workflow-management');
  } finally {
    pageLoading.value = false;
  }
};

const submitWorkflow = async () => {
  isLoading.value = true;
  try {
    const payload = {
      workflowName: form.workflowName,
      description: form.description,
      isActive: form.isActive,
      trigger: form.trigger,
      nodes: form.nodes.map(node => ({
        nodeName: node.nodeName,
        stepOrder: node.stepOrder,
        completionRule: node.completionRule,
        assignmentType: node.assignmentType,
        assignedRank: node.assignmentType === 'DYNAMIC_ROLE_IN_DISTRICT' ? node.assignedRank : null,
        staticUserIds: node.assignmentType === 'STATIC_USER_LIST' ? node.staticUserIds : []
      }))
    };

    if (isEditMode.value) {
      await apiClient.put(`/admin/workflows/${workflowId.value}`, payload);
      alert('工作流更新成功！');
    } else {
      await apiClient.post('/admin/workflows', payload);
      alert('工作流创建成功！');
    }
    router.push('/workflow-management');
  } catch (err) {
    alert('操作失败：' + (err.response?.data?.message || err.message));
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  pageLoading.value = true;
  await Promise.all([
    fetchDropdownData(),
    fetchAssignableUsers(null)
  ]);

  if (isEditMode.value) {
    await loadWorkflowForEdit();
    if (form.trigger.districtId) {
      await fetchAssignableUsers(form.trigger.districtId);
    }
  } else {
    if (form.nodes.length === 0) {
      addNode();
    }
  }
  pageLoading.value = false;
});
</script>
