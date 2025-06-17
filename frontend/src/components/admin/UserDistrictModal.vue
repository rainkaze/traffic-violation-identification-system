<template>
  <div v-if="show" class="fixed inset-0 bg-black bg-opacity-50 z-40 flex justify-center items-center" @click.self="close">
    <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md mx-4">
      <h3 class="text-xl font-bold mb-4">为 {{ user?.fullName }} 分配辖区</h3>
      <div v-if="user">
        <div class="space-y-2">
          <div v-for="district in allDistricts" :key="district.districtId">
            <label class="flex items-center">
              <input type="checkbox" :value="district.districtId" v-model="selectedDistricts" class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary">
              <span class="ml-3 text-sm text-gray-700">{{ district.districtName }}</span>
            </label>
          </div>
        </div>
        <div v-if="error" class="text-red-600 text-sm mt-4">{{ error }}</div>
        <div class="mt-6 flex justify-end gap-3">
          <button type="button" @click="close" class="btn btn-secondary">取消</button>
          <button @click="saveDistricts" :disabled="isLoading" class="btn btn-primary">
            {{ isLoading ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import apiClient from '@/services/api';

const props = defineProps({
  show: Boolean,
  user: Object,
  allDistricts: Array,
});

const emit = defineEmits(['close', 'save']);

const selectedDistricts = ref([]);
const error = ref('');
const isLoading = ref(false);

// 监听 user 和 allDistricts props 的变化
watch(() => [props.user, props.allDistricts], ([newUser, newDistricts]) => {
  if (newUser && newUser.districts && newDistricts && newDistricts.length > 0) {
    // 增加一个防御性检查，过滤掉数组中可能存在的 null 或 undefined 值
    const validDistricts = newDistricts.filter(d => d);

    const userDistrictIds = validDistricts
      .filter(d => newUser.districts.includes(d.districtName))
      .map(d => d.districtId);
    selectedDistricts.value = userDistrictIds;
  } else {
    selectedDistricts.value = [];
  }
}, { immediate: true, deep: true });

const close = () => {
  emit('close');
};

const saveDistricts = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    await apiClient.put(`/admin/users/${props.user.userId}/districts`, {
      districtIds: selectedDistricts.value
    });
    emit('save');
    close();
  } catch (err) {
    error.value = err.response?.data?.message || '保存失败';
  } finally {
    isLoading.value = false;
  }
};
</script>
