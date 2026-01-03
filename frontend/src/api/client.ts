import axios, { AxiosInstance, AxiosError } from 'axios'

const API_BASE_URL = '/api/v1'

class ApiClient {
  private client: AxiosInstance

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    // Request interceptor для добавления токена
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => Promise.reject(error)
    )

    // Response interceptor для обработки ошибок
    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        if (error.response?.status === 401) {
          // Попытка обновить токен
          const refreshToken = localStorage.getItem('refreshToken')
          const username = localStorage.getItem('username')
          
          if (refreshToken && username) {
            try {
              const response = await this.client.post('/auth/update-token', {
                username,
                token: refreshToken
              })
              
              if (response.data.data?.accessToken) {
                localStorage.setItem('accessToken', response.data.data.accessToken)
                // Повторяем оригинальный запрос
                if (error.config) {
                  error.config.headers.Authorization = `Bearer ${response.data.data.accessToken}`
                  return this.client.request(error.config)
                }
              }
            } catch (refreshError) {
              // Если обновление токена не удалось, очищаем хранилище
              localStorage.clear()
              window.location.href = '/login'
            }
          }
        }
        return Promise.reject(error)
      }
    )
  }

  // Auth endpoints
  async register(username: string, email: string, password: string) {
    return this.client.post('/auth/register', { username, email, password })
  }

  async login(username: string, password: string) {
    return this.client.post('/auth/login', { username, password })
  }

  async logout(username: string, token: string) {
    return this.client.post('/auth/logout', { username, token })
  }

  async updateToken(username: string, token: string) {
    return this.client.post('/auth/update-token', { username, token })
  }

  // Advertisement endpoints
  async getAdvertisements(params?: {
    userId?: string
    page?: number
    size?: number
    sortBy?: string
  }) {
    return this.client.get('/core', { params })
  }

  async getUserAdvertisements(userId: string) {
    return this.client.get(`/core/user/${userId}`)
  }

  async createAdvertisement(data: {
    username: string
    token: string
    title: string
    description: string
    type: string
    price: number
  }) {
    return this.client.post('/core/make-advertisement', data)
  }

  async editAdvertisement(data: {
    username: string
    token: string
    advertisementId: string
    title?: string
    description?: string
    type?: string
    price?: number
  }) {
    return this.client.patch('/core/edit-advertisement', data)
  }

  async deleteAdvertisement(data: {
    username: string
    token: string
    advertisementId: string
  }) {
    return this.client.delete('/core', { data })
  }

  // Balance endpoints
  async getBalance(userId: string) {
    return this.client.get(`/balance/${userId}`)
  }

  async deposit(data: { userId: string; amount: number; token: string }) {
    return this.client.post('/balance/deposit', data)
  }

  async withdraw(data: { userId: string; amount: number; token: string }) {
    return this.client.post('/balance/withdraw', data)
  }

  // Notification endpoints
  async getNotifications(userId: string) {
    return this.client.get(`/notifications/${userId}`)
  }

  async getUnreadNotifications(userId: string) {
    return this.client.get(`/notifications/${userId}/unread`)
  }

  async markNotificationAsRead(notificationId: string) {
    return this.client.post(`/notifications/read/${notificationId}`)
  }

  async markAllNotificationsAsRead(userId: string) {
    return this.client.post(`/notifications/read-all/${userId}`)
  }

  // Moderation endpoints (admin only)
  async moderate(data: {
    advertisementId: string
    moderatorId: string
    decision: string
    comment?: string
  }) {
    return this.client.post('/admin/moderation', data)
  }

  async getModerationResult(advertisementId: string) {
    return this.client.get(`/admin/moderation/${advertisementId}`)
  }
}

export default new ApiClient()