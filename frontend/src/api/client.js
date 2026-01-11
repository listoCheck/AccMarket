import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to add token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle token refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    
    // Проверяем, что это ошибка 401 и мы еще не пытались обновить токен
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        const username = localStorage.getItem('username')
        
        if (refreshToken && username) {
          console.log('Access token expired, refreshing...')
          
          // Используем отдельный axios instance для обновления токена
          const refreshResponse = await axios.post('/api/v1/auth/update-token', {
            username,
            token: refreshToken
          })
          
          console.log('Token refresh response:', refreshResponse.data)
          
          if (refreshResponse.data.code === 200 && refreshResponse.data.body) {
            const newAccessToken = refreshResponse.data.body.accessToken
            const newRefreshToken = refreshResponse.data.body.refreshToken
            
            // Обновляем токены в localStorage
            localStorage.setItem('accessToken', newAccessToken)
            if (newRefreshToken) {
              localStorage.setItem('refreshToken', newRefreshToken)
            }
            
            // Обновляем заголовок Authorization для повторного запроса
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
            
            console.log('Token refreshed successfully, retrying original request')
            
            // Повторяем оригинальный запрос с новым токеном
            return apiClient(originalRequest)
          } else {
            console.error('Token refresh failed:', refreshResponse.data.message)
            throw new Error('Token refresh failed')
          }
        } else {
          console.error('No refresh token or username found')
          throw new Error('No refresh token available')
        }
      } catch (refreshError) {
        console.error('Token refresh error:', refreshError)
        
        // Очищаем все данные и перенаправляем на страницу входа
        localStorage.clear()
        window.location.href = '/login'
        
        return Promise.reject(refreshError)
      }
    }
    
    return Promise.reject(error)
  }
)

export default apiClient