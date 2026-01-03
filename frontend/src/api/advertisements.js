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
    return apiClient.delete('/core', { data })
  }
}