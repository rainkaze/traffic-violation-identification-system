// stores/systemConfig.js
import apiClient from '@/services/api'
import { defineStore } from 'pinia'

export const useSystemConfigStore = defineStore('systemConfig', {
  state: () => ({
    systemName: '',
    sessionTimeout: 0,
    dataRetentionDays: 0
  }),
  actions: {
    async fetchConfig() {
      const res = await apiClient.get('/system/config')
      this.systemName = res.data.systemName
      this.sessionTimeout = res.data.sessionTimeout
      this.dataRetentionDays = res.data.dataRetentionDays
    },
    async saveConfig() {
      const payload = {
        systemName: this.systemName,
        sessionTimeout: this.sessionTimeout,
        dataRetentionDays: this.dataRetentionDays
      }
      await apiClient.put('/system/config', payload)
    }
  }
})
