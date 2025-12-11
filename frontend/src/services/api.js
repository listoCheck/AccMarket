import axios from 'axios'

const API_BASE_URL = '/api/v1'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptor для добавления токена к запросам
api.interceptors.request.use(
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

// Interceptor для обработки ошибок
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Попытка обновить токен
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const username = localStorage.getItem('username')
          const response = await axios.post(`${API_BASE_URL}/auth/update-token`, {
            username,
            token: refreshToken
          })
          
          if (response.data.body?.accessToken) {
            localStorage.setItem('accessToken', response.data.body.accessToken)
            error.config.headers.Authorization = `Bearer ${response.data.body.accessToken}`
            return api.request(error.config)
          }
        } catch (refreshError) {
          localStorage.clear()
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

export default {
  // Auth endpoints
  auth: {
    register(username, email, password) {
      return api.post('/auth/register', { username, email, password })
    },
    login(username, password) {
      return api.post('/auth/login', { username, password })
    },
    logout(username, token) {
      return api.post('/auth/logout', { username, token })
    },
    updateToken(username, token) {
      return api.post('/auth/update-token', { username, token })
    }
  },

  // Advertisement endpoints
  advertisements: {
    getAll(params = {}) {
      return api.get('/core', { params })
    },
    getUserAdvertisements(userId) {
      return api.get(`/core/user/${userId}`)
    },
    create(data) {
      return api.post('/core/make-advertisement', data)
    },
    update(data) {
      return api.patch('/core/edit-advertisement', data)
    },
    delete(data) {
      return api.delete('/core', { data })
    }
  },

  // Admin endpoints
  admin: {
    getAllUsers(username, token) {
      return api.get('/admin/roles/all-users', {
        params: { request: { username, token } },
        data: { username, token }
      })
    },
    getUserRoles(username, token, targetUsername) {
      return api.get('/admin/roles/user', {
        params: { request: { username, token, targetUsername } },
        data: { username, token, targetUsername }
      })
    },
    updateUserRoles(data) {
      return api.patch('/admin/roles/update', data)
    },
    assignAdmin(data) {
      return api.post('/admin/roles/assign-admin', data)
    },
    assignModerator(data) {
      return api.post('/admin/roles/assign-moderator', data)
    },
    getAdvertisements(params = {}) {
      return api.get('/admin/roles/get-advertisements', { params })
    }
  }
}