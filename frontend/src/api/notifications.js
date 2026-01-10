import apiClient from './client'

export const notificationsAPI = {
  getAll(userId) {
    return apiClient.get(`/notifications/user/${userId}`)
  },

  getUnread(userId) {
    return apiClient.get(`/notifications/user/${userId}/unread`)
  },

  markAsRead(notificationId) {
    return apiClient.post(`/notifications/read/${notificationId}`)
  },

  markAllAsRead(userId) {
    return apiClient.post(`/notifications/read-all/${userId}`)
  }
}