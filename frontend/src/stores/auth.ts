import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import apiClient from '@/api/client'

export const useAuthStore = defineStore('auth', () => {
  const username = ref<string | null>(localStorage.getItem('username'))
  const userId = ref<string | null>(localStorage.getItem('userId'))
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const email = ref<string | null>(localStorage.getItem('email'))

  const isAuthenticated = computed(() => !!accessToken.value && !!username.value)

  async function register(user: { username: string; email: string; password: string }) {
    try {
      const response = await apiClient.register(user.username, user.email, user.password)
      if (response.data.success) {
        return { success: true, message: response.data.message }
      }
      return { success: false, message: response.data.message }
    } catch (error: any) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'Ошибка регистрации' 
      }
    }
  }

  async function login(credentials: { username: string; password: string }) {
    try {
      const response = await apiClient.login(credentials.username, credentials.password)
      
      if (response.data.success && response.data.data) {
        const { accessToken: newAccessToken, refreshToken: newRefreshToken, userId: newUserId, email: userEmail } = response.data.data
        
        // Сохраняем данные
        username.value = credentials.username
        userId.value = newUserId
        accessToken.value = newAccessToken
        refreshToken.value = newRefreshToken
        email.value = userEmail

        // Сохраняем в localStorage
        localStorage.setItem('username', credentials.username)
        localStorage.setItem('userId', newUserId)
        localStorage.setItem('accessToken', newAccessToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        localStorage.setItem('email', userEmail)

        return { success: true, message: 'Успешный вход' }
      }
      
      return { success: false, message: response.data.message || 'Ошибка входа' }
    } catch (error: any) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'Неверный логин или пароль' 
      }
    }
  }

  async function logout() {
    try {
      if (username.value && refreshToken.value) {
        await apiClient.logout(username.value, refreshToken.value)
      }
    } catch (error) {
      console.error('Ошибка при выходе:', error)
    } finally {
      // Очищаем данные
      username.value = null
      userId.value = null
      accessToken.value = null
      refreshToken.value = null
      email.value = null

      // Очищаем localStorage
      localStorage.clear()
    }
  }

  async function updateAccessToken() {
    try {
      if (username.value && refreshToken.value) {
        const response = await apiClient.updateToken(username.value, refreshToken.value)
        
        if (response.data.success && response.data.data?.accessToken) {
          accessToken.value = response.data.data.accessToken
          localStorage.setItem('accessToken', response.data.data.accessToken)
          return true
        }
      }
      return false
    } catch (error) {
      console.error('Ошибка обновления токена:', error)
      await logout()
      return false
    }
  }

  return {
    username,
    userId,
    accessToken,
    refreshToken,
    email,
    isAuthenticated,
    register,
    login,
    logout,
    updateAccessToken
  }
})