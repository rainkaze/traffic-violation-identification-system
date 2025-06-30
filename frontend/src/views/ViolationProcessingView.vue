<template>
  <div class="p-4 sm:p-6 lg:p-8">
    <div v-if="isLoading" class="text-center p-10">正在加载处理详情...</div>
    <div v-else-if="error" class="card text-danger bg-red-50 p-4">{{ error }}</div>
    <div v-else-if="details" class="card max-w-5xl mx-auto">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">处理违法记录：{{ details.violation.plate }}</h2>
      <p class="text-sm text-gray-500 mb-6">
        案件编号: V-{{ details.violation.id }} |
        处理模式: <span :class="details.workflowCase ? 'font-bold text-primary' : 'font-bold text-gray-700'">{{ details.workflowCase ? '工作流处理' : '单节点处理' }}</span>
      </p>

      <div class="flex mb-8" v-if="details.workflowCase && details.workflowNodes">
        <div v-for="(node, index) in details.workflowNodes" :key="node.nodeId" class="step flex-1" :class="{ 'step-active': details.currentStep >= (index + 1) }">
          <div class="step-dot" :class="{ 'step-dot-active': details.currentStep >= (index + 1) }">
            <i v-if="details.currentStep > (index + 1)" class="fa fa-check"></i>
            <span v-else>{{ index + 1 }}</span>
          </div>
          <span class="ml-2">{{ node.nodeName }}</span>
          <div v-if="index < details.workflowNodes.length - 1" class="step-line" :class="{ 'step-line-active': details.currentStep > (index + 1) }"></div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div>
          <h3 class="font-semibold text-lg border-b pb-2 mb-4">违法详情</h3>
          <div class="space-y-2 text-sm">
            <p><strong>车牌号:</strong> <span class="font-mono bg-gray-100 px-2 py-1 rounded">{{ details.violation.plate }}</span></p>
            <p><strong>违法类型:</strong> {{ details.violation.type }}</p>
            <p><strong>违法时间:</strong> {{ new Date(details.violation.time).toLocaleString() }}</p>
            <p><strong>违法地点:</strong> {{ details.violation.location }}</p>
            <p><strong>所属辖区:</strong> {{ details.violation.district }}</p>
            <p><strong>抓拍设备:</strong> {{ details.violation.device }}</p>
          </div>
          <h4 class="font-semibold mt-6 mb-2">证据照片</h4>
          <div class="bg-gray-200 rounded-lg aspect-video">
            <img
              v-if="details.violation.evidenceImageUrls && details.violation.evidenceImageUrls.length > 0"
              :src="details.violation.evidenceImageUrls[0]"
              alt="违法证据"
              class="w-full h-full object-contain rounded-lg"
            >
            <div v-else class="flex items-center justify-center h-full">
              <p class="text-gray-500">(暂无图片)</p>
            </div>
          </div>
        </div>

        <div>
          <h3 class="font-semibold text-lg border-b pb-2 mb-4">处理意见</h3>
          <div v-if="!details.currentUserAssigned" class="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 mb-4" role="alert">
            <p class="font-bold">只读模式</p>
            <p>您不是当前节点的处理人或任务已处理，仅可查看。</p>
          </div>
          <form @submit.prevent="submitDecision('APPROVE')" class="space-y-4">
            <div>
              <label for="remarks" class="block text-sm font-medium text-gray-700">备注</label>
              <textarea id="remarks" v-model="form.remarks" rows="4" class="input w-full mt-1" :disabled="!details.currentUserAssigned"></textarea>
            </div>
            <div v-if="details.currentUserAssigned" class="flex justify-end gap-3">
              <button type="button" @click="submitDecision('REJECT')" class="btn btn-danger" :disabled="isSubmitting">驳回</button>
              <button type="submit" class="btn btn-success" :disabled="isSubmitting">
                <i class="fa fa-spinner fa-spin" v-if="isSubmitting"></i>
                {{ isSubmitting ? '提交中...' : (details.workflowCase ? '批准并提交' : '确认处理') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 1. 从 'vue' 中引入 watch
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import apiClient from '@/services/api';

const props = defineProps({ id: String });
const route = useRoute();
const router = useRouter();

const isLoading = ref(true);
const isSubmitting = ref(false);
const error = ref(null);
const details = ref(null);

const form = reactive({
  decision: 'APPROVE',
  remarks: '',
  isWorkflowCase: false,
});

const fetchDetails = async () => {
  // 增加一个保护，如果id不存在则不执行
  if (!props.id) return;
  isLoading.value = true;
  error.value = null;
  try {
    const response = await apiClient.get(`/processing/details/${props.id}`);
    details.value = response.data;
    form.isWorkflowCase = details.value.workflowCase;
    console.log("成功获取到的详情数据:", details.value);
  } catch (err) {
    const errorMessage = '加载处理详情失败: ' + (err.response?.data?.message || '未知错误');
    error.value = errorMessage;
    console.error(errorMessage, err);
  } finally {
    isLoading.value = false;
  }
};

const initiateProcessing = async () => {
  if (!props.id) return;
  try {
    await apiClient.post(`/processing/initiate/${props.id}`);
    console.log("处理流程启动请求已成功发送。");
  } catch (err) {
    console.warn("启动处理流程的请求失败（这可能不影响您查看详情）:", err.response?.data?.message || err.message);
  }
};

const submitDecision = async (decisionType = 'APPROVE') => {
  // ... 此函数保持不变 ...
  form.decision = decisionType;
  isSubmitting.value = true;
  try {
    await apiClient.post(`/processing/submit/${props.id}`, form);
    alert('处理成功！');
    router.push('/violations');
  } catch (err) {
    alert('处理失败: ' + (err.response?.data?.message || err.message));
  } finally {
    isSubmitting.value = false;
  }
};

// 2. 【核心修正】使用 watch 来侦听 props.id 的变化
watch(
  () => props.id, // 我们要侦听的目标
  (newId, oldId) => {
    // 当 newId 存在且与 oldId 不同时，重新加载数据
    if (newId) {
      console.log(`路由ID从 ${oldId} 变为 ${newId}，重新加载数据...`);
      fetchDetails();
      initiateProcessing();
    }
  },
  { immediate: true } // immediate: true 确保组件第一次加载时也会执行一次，这样就可以替代 onMounted
);

/*
// 3. 【替代】原来的 onMounted 现在可以被 watch 的 immediate:true 选项替代，所以可以注释或删除
onMounted(() => {
  // fetchDetails();
  // initiateProcessing();
});
*/
</script>
