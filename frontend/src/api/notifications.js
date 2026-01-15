import apiClient from './client'

export const notificationsAPI = {
  getAll(userId) {
    return apiClient.get(`/notifications/user/${userId}`)
  },

  getUnread(userId) {
    return apiClient.get(`/notifications/user/${userId}/unread`)
  },

  markAsRead(notificationId, userId) {
    return apiClient.post(`/notifications/read/${notificationId}?userId=${userId}`)
  },

  markAllAsRead(userId) {
    return apiClient.post(`/notifications/read-all/${userId}`)
  }
}