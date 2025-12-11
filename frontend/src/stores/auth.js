import { defineStore } from 'pinia'
import api from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    username: localStorage.getItem('username') || null,
    roles: JSON.parse(localStorage.getItem('roles') || '[]'),
    isAuthenticated: !!localStorage.getItem('accessToken')
  }),

  getters: {
    isAdmin: (state) => state.roles.includes('ROLE_ADMIN'),
    isModerator: (state) => state.roles.includes('ROLE_MODERATOR'),
    hasAdminAccess: (state) => state.roles.includes('ROLE_ADMIN') || state.roles.includes('ROLE_MODERATOR')
  },

  actions: {
    async register(username, email, password) {
      try {
        const response = await api.auth.register(username, email, password)
        if (response.data.code === 200) {
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
        const response = await api.auth.login(username, password)
        if (response.data.code === 200 && response.data.body) {
          this.accessToken = response.data.body.accessToken
          this.refreshToken = response.data.body.refreshToken
          this.username = username
          this.isAuthenticated = true
          
          // Получаем роли пользователя
          const roles = response.data.body.roles || []
          this.roles = roles
          
          localStorage.setItem('accessToken', this.accessToken)
          localStorage.setItem('refreshToken', this.refreshToken)
          localStorage.setItem('username', username)
          localStorage.setItem('roles', JSON.stringify(roles))
          
          return { success: true, message: response.data.message }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        return {
          success: false,
          message: error.response?.data?.message || 'Ошибка авторизации'
        }
      }
    },

    async logout() {
      try {
        if (this.username && this.accessToken) {
          await api.auth.logout(this.username, this.accessToken)
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
      this.roles = []
      this.isAuthenticated = false
      
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('username')
      localStorage.removeItem('roles')
    }
  }
})