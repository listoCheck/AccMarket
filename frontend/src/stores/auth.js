import { defineStore } from 'pinia'
import { authAPI } from '../api/auth'

// Функция для декодирования JWT токена
function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (e) {
    console.error('Error parsing JWT:', e)
    return null
  }
}

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
        
        // Проверяем, что в ответе есть body с токенами
        if (response.data && response.data.body) {
          const data = response.data.body
          
          // Сохраняем токены и данные пользователя
          this.accessToken = data.accessToken
          this.refreshToken = data.refreshToken
          this.username = username
          this.userId = data.userId
          
          // Извлекаем роли из JWT токена
          const tokenPayload = parseJwt(data.accessToken)
          this.roles = tokenPayload?.roles || ['USER']

          localStorage.setItem('accessToken', data.accessToken)
          localStorage.setItem('refreshToken', data.refreshToken)
          localStorage.setItem('username', username)
          localStorage.setItem('userId', data.userId)
          localStorage.setItem('roles', JSON.stringify(this.roles))

          console.log('Registration successful, user logged in automatically, roles:', this.roles)
          return { success: true, message: 'Регистрация успешна!' }
        }
        
        return { success: false, message: response.data.message || 'Ошибка регистрации' }
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
          
          // Извлекаем роли из JWT токена
          const tokenPayload = parseJwt(data.accessToken)
          console.log('Token payload:', tokenPayload)
          this.roles = tokenPayload?.roles || ['USER']

          localStorage.setItem('accessToken', data.accessToken)
          localStorage.setItem('refreshToken', data.refreshToken)
          localStorage.setItem('username', username)
          localStorage.setItem('userId', data.userId)
          localStorage.setItem('roles', JSON.stringify(this.roles))

          console.log('Login data saved successfully, roles:', this.roles)
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
        if (response.data.code === 200 && response.data.body) {
          const newAccessToken = response.data.body.accessToken
          const newRefreshToken = response.data.body.refreshToken
          
          this.accessToken = newAccessToken
          localStorage.setItem('accessToken', newAccessToken)
          
          if (newRefreshToken) {
            this.refreshToken = newRefreshToken
            localStorage.setItem('refreshToken', newRefreshToken)
          }
          
          return true
        }
        
        this.clearAuth()
        return false
      } catch (error) {
        this.clearAuth()
        return false
      }
    },

    // Метод для синхронизации токенов из localStorage (вызывается interceptor'ом)
    syncTokensFromStorage() {
      this.accessToken = localStorage.getItem('accessToken')
      this.refreshToken = localStorage.getItem('refreshToken')
      this.username = localStorage.getItem('username')
      this.userId = localStorage.getItem('userId')
      
      // Пытаемся извлечь роли из токена, если он есть
      if (this.accessToken) {
        const tokenPayload = parseJwt(this.accessToken)
        this.roles = tokenPayload?.roles || JSON.parse(localStorage.getItem('roles') || '[]')
      } else {
        this.roles = JSON.parse(localStorage.getItem('roles') || '[]')
      }
    }
  }
})