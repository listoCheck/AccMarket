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
        console.log('Register response:', response.data)
        
        if (response.data.success) {
          return { success: true, message: response.data.message || 'Регистрация успешна!' }
        }
        return { success: false, message: response.data.message }
      } catch (error) {
        console.error('Register error:', error)
        return {
          success: false,
          message: error.response?.data?.message || 'Ошибка регистрации'
        }
      }
    },

    async login(username, password) {
      try {
        const response = await authAPI.login(username, password)
        console.log('Login response:', response.data)
        
        // Проверяем, что в ответе есть body с данными
        if (response.data && response.data.body) {
          const data = response.data.body
          this.accessToken = data.accessToken
          this.refreshToken = data.refreshToken
          this.username = username
          this.userId = data.userId
          
          // Роли нужно получить отдельно или из токена
          // Пока устанавливаем базовую роль USER
          this.roles = ['USER']

          localStorage.setItem('accessToken', data.accessToken)
          localStorage.setItem('refreshToken', data.refreshToken)
          localStorage.setItem('username', username)
          localStorage.setItem('userId', data.userId)
          localStorage.setItem('roles', JSON.stringify(['USER']))

          console.log('Login data saved successfully')
          return { success: true }
        }
        
        return { success: false, message: response.data.message || 'Ошибка входа' }
      } catch (error) {
        console.error('Login error:', error)
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