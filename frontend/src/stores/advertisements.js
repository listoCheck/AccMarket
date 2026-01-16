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
        console.log('API response for user advertisements:', response.data)
        console.log('Content:', response.data.content)
        console.log('Content length:', response.data.content?.length)
        
        this.advertisements = response.data.content || []
        this.pagination = {
          page: response.data.number || 0,
          size: response.data.size || 10,
          totalPages: response.data.totalPages || 0,
          totalElements: response.data.totalElements || 0
        }
        
        console.log('Advertisements set to:', this.advertisements)
        console.log('Advertisements length:', this.advertisements.length)
        return { success: true }
      } catch (error) {
        console.error('Error in fetchUserAdvertisements:', error)
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
        if (response.data.code === 200) {
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
        if (response.data.code === 200) {
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
        console.log('Delete response:', response.data)
        if (response.data.code === 200) {
          // Удаляем объявление из локального массива
          this.advertisements = this.advertisements.filter(ad => ad.id !== data.advertisementId)
          console.log('Advertisement removed from store')
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        console.error('Delete error:', error)
        this.error = error.response?.data?.message || 'Ошибка удаления объявления'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async buyAdvertisement(advertisementId) {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.buy(advertisementId)
        if (response.data.code === 200) {
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка покупки объявления'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async fetchBoughtAdvertisements() {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.getBought()
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка загрузки купленных объявлений'
        return { success: false, message: this.error }
      } finally {
        this.loading = false
      }
    },

    async fetchCreatedAdvertisements() {
      this.loading = true
      this.error = null
      try {
        const response = await advertisementsAPI.getCreated()
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || 'Ошибка загрузки созданных объявлений'
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