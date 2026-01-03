import apiClient from './client'

export const balanceAPI = {
  get(userId) {
    return apiClient.get(`/balance/${userId}`)
  },

  deposit(data) {
    return apiClient.post('/balance/deposit', data)
  },

  withdraw(data) {
    return apiClient.post('/balance/withdraw', data)
  }
}