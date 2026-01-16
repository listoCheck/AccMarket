import apiClient from './client'

export const adminAPI = {
  assignAdmin(data) {
    return apiClient.post('/admin/roles/assign-admin', data)
  },

  assignModerator(data) {
    return apiClient.post('/admin/roles/assign-moderator', data)
  },

  getUserRoles(data) {
    return apiClient.post('/admin/roles/user', data)
  },

  updateUserRoles(data) {
    return apiClient.patch('/admin/roles/update', data)
  },

  getAllUsers(data) {
    return apiClient.post('/admin/roles/all-users', data)
  },

  getAdvertisements(params = {}) {
    return apiClient.get('/admin/roles/get-advertisements', { params })
  }
}

export const moderationAPI = {
  moderate(data) {
    return apiClient.post('/admin/moderation', data)
  },

  getByAdvertisement(advertisementId) {
    return apiClient.get(`/admin/moderation/${advertisementId}`)
  }
}

export const appealsAPI = {
  create(data) {
    return apiClient.post('/appeals', data)
  },

  decide(data) {
    return apiClient.post('/appeals/decide', data)
  },

  getPending() {
    return apiClient.get('/appeals/pending')
  }
}