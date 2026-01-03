import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/api/client'

export interface Advertisement {
  id: string
  title: string
  description: string
  type: string
  price: number
  userId: string
  username: string
  createdAt: string
  status: string
}

export const useAdvertisementsStore = defineStore('advertisements', () => {
  const advertisements = ref<Advertisement[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const currentPage = ref(0)
  const totalPages = ref(0)
  const pageSize = ref(10)

  async function fetchAdvertisements(params?: {
    userId?: string
    page?: number
    size?: number
    sortBy?: string
  }) {
    loading.value = true
    error.value = null
    
    try {
      const response = await apiClient.getAdvertisements(params)
      
      if (response.data) {
        advertisements.value = response.data.content || []
        currentPage.value = response.data.number || 0
        totalPages.value = response.data.totalPages || 0
        pageSize.value = response.data.size || 10
      }
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Ошибка загрузки объявлений'
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  async function fetchUserAdvertisements(userId: string) {
    loading.value = true
    error.value = null
    
    try {
      const response = await apiClient.getUserAdvertisements(userId)
      
      if (response.data) {
        advertisements.value = response.data.content || []
      }
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Ошибка загрузки объявлений пользователя'
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  async function createAdvertisement(data: {
    username: string
    token: string
    title: string
    description: string
    type: string
    price: number
  }) {
    loading.value = true
    error.value = null
    
    try {
      const response = await apiClient.createAdvertisement(data)
      
      if (response.data.success) {
        // Обновляем список объявлений
        await fetchAdvertisements()
        return { success: true, message: response.data.message }
      }
      
      return { success: false, message: response.data.message }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Ошибка создания объявления'
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  async function editAdvertisement(data: {
    username: string
    token: string
    advertisementId: string
    title?: string
    description?: string
    type?: string
    price?: number
  }) {
    loading.value = true
    error.value = null
    
    try {
      const response = await apiClient.editAdvertisement(data)
      
      if (response.data.success) {
        // Обновляем список объявлений
        await fetchAdvertisements()
        return { success: true, message: response.data.message }
      }
      
      return { success: false, message: response.data.message }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Ошибка редактирования объявления'
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  async function deleteAdvertisement(data: {
    username: string
    token: string
    advertisementId: string
  }) {
    loading.value = true
    error.value = null
    
    try {
      const response = await apiClient.deleteAdvertisement(data)
      
      if (response.data.success) {
        // Обновляем список объявлений
        await fetchAdvertisements()
        return { success: true, message: response.data.message }
      }
      
      return { success: false, message: response.data.message }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Ошибка удаления объявления'
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    advertisements,
    loading,
    error,
    currentPage,
    totalPages,
    pageSize,
    fetchAdvertisements,
    fetchUserAdvertisements,
    createAdvertisement,
    editAdvertisement,
    deleteAdvertisement
  }
})