import apiClient from './client'

export const authAPI = {
  register(username, email, password) {
    return apiClient.post('/auth/register', {
      username,
      email,
      password
    })
  },

  login(username, password) {
    return apiClient.post('/auth/login', {
      username,
      password
    })
  },

  logout(username, token) {
    return apiClient.post('/auth/logout', {
      username,
      token
    })
  },

  updateToken(username, token) {
    return apiClient.post('/auth/update-token', {
      username,
      token
    })
  }
}