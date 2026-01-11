import apiClient from './client'

export const advertisementsAPI = {
  getAll(params = {}) {
    return apiClient.get('/core', { params })
  },

  getUserAdvertisements(userId) {
    return apiClient.get(`/core/user/${userId}`)
  },

  create(data) {
    return apiClient.post('/core/make-advertisement', data)
  },

  update(data) {
    return apiClient.patch('/core/edit-advertisement', data)
  },

  delete(data) {
    return apiClient.post('/core', data)
  },

  buy(advertisementId) {
    return apiClient.post('/core/buy', { advertisementId })
  },

  getBought() {
    return apiClient.get('/core/cabinet/buyer')
  },

  getCreated() {
    return apiClient.get('/core/cabinet/seller')
  }
}