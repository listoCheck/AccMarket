import { defineStore } from 'pinia'
import { authAPI } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    username: localStorage.getItem('username') || null,
    userId: localStorage.getItem('userId') || null,
    roles: JSON.parse(localStorage.getItem('roles') || '[]')
  }),

  getters: {
    isAuthenticated: (state) => !!state.accessToken,
    isAdmin: (state) => state.roles.includes('ADMIN'),
    isModerator: (state) => state.roles.includes('MODERATOR') || state.roles.includes('ADMIN')
  },

  actions: {
    async register(username, email, password) {
      try {
        const response = await authAPI.register(username, email, password)
        if (response.data.success) {
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || 'Ошибка регистрации' 
        }
      }
    },

    async login(username, password) {
      try {
        const response = await authAPI.login(username, password)
        if (response.data.success) {
          const data = response.data.data
          this.accessToken = data.accessToken
          this.refreshToken = data.refreshToken
          this.username = username
          this.userId = data.userId
          this.roles = data.roles || []

          localStorage.setItem('accessToken', data.accessToken)
          localStorage.setItem('refreshToken', data.refreshToken)
          localStorage.setItem('username', username)
          localStorage.setItem('userId', data.userId)
          localStorage.setItem('roles', JSON.stringify(data.roles || []))

          return { success: true }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        return { 
          success: false, 
          message: error.response?.data?.message || 'Ошибка входа' 
        }
      }
    },

    async logout() {
      try {
        if (this.username && this.accessToken) {
          await authAPI.logout(this.username, this.accessToken)
        }
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.clearAuth()
      }
    },

    clearAuth() {
      this.user = null
      this.accessToken = null
      this.refreshToken = null
      this.username = null
      this.userId = null
      this.roles = []
      
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
      localStorage.removeItem('roles')
    },

    async refreshAccessToken() {
      try {
        if (!this.refreshToken || !this.username) {
          this.clearAuth()
          return false
        }

        const response = await authAPI.updateToken(this.username, this.refreshToken)
        if (response.data.success) {
          const newAccessToken = response.data.data.accessToken
          this.accessToken = newAccessToken
          localStorage.setItem('accessToken', newAccessToken)
          return true
        }
        
        this.clearAuth()
        return false
      } catch (error) {
        this.clearAuth()
        return false
      }
    }
  }
})