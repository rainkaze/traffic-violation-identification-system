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
          <div class="bg-gray-200 rounded-lg aspect-video flex items-center justify-center">
            <p class="text-gray-500">(暂无图片)</p>
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
// (JS部分与上一版相同，无需修改)
import { ref, reactive, onMounted } from 'vue';
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
  isLoading.value = true;
  error.value = null;
  try {
    const response = await apiClient.get(`/processing/details/${props.id}`);
    details.value = response.data;
    form.isWorkflowCase = details.value.workflowCase;
  } catch (err) {
    error.value = '加载处理详情失败: ' + (err.response?.data?.message || '未知错误');
  } finally {
    isLoading.value = false;
  }
};

const submitDecision = async (decisionType = 'APPROVE') => {
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
}

onMounted(async () => {
  // 页面加载时，先调用 initiate 接口
  try {
    await apiClient.post(`/processing/initiate/${props.id}`);
    // 不管结果如何，都去获取详情
    await fetchDetails();
  } catch (err) {
    error.value = "启动处理流程失败：" + (err.response?.data?.message || err.message);
    isLoading.value = false;
  }
});
</script>
