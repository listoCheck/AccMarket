import { defineStore } from 'pinia'
import { advertisementsAPI } from '../api/advertisements'

export const useAdvertisementsStore = defineStore('advertisements', {
  state: () => ({
    advertisements: [],
    currentAdvertisement: null,
    loading: false,
    error: null,
    pagination: {
      page: 0,
      size: 10,
      totalPages: 0,
      totalElements: 0
    }
  }),

  actions: {
    async fetchAdvertisements(params = {}) {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.getAll(params)
        this.advertisements = response.data.content
        this.pagination = {
          page: response.data.number,
          size: response.data.size,
          totalPages: response.data.totalPages,
          totalElements: response.data.totalElements
        }
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка загрузки объявлений'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async fetchUserAdvertisements(userId) {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.getUserAdvertisements(userId)
        this.advertisements = response.data.content
        this.pagination = {
          page: response.data.number,
          size: response.data.size,
          totalPages: response.data.totalPages,
          totalElements: response.data.totalElements
        }
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка загрузки объявлений'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async createAdvertisement(data) {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.create(data)
        if (response.data.success) {
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка создания объявления'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async updateAdvertisement(data) {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.update(data)
        if (response.data.success) {
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка обновления объявления'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async deleteAdvertisement(data) {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.delete(data)
        if (response.data.success) {
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка удаления объявления'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    setCurrentAdvertisement(advertisement) {
      this.currentAdvertisement = advertisement
    },

    clearCurrentAdvertisement() {
      this.currentAdvertisement = null
    }
  }
})