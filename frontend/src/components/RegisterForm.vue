<template>
  <div class="auth-form">
    <h2>Регистрация</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="username">Имя пользователя</label>
        <input
          id="username"
          v-model="username"
          type="text"
          required
          placeholder="Введите имя пользователя"
        />
      </div>
      
      <div class="form-group">
        <label for="email">Email</label>
        <input
          id="email"
          v-model="email"
          type="email"
          required
          placeholder="Введите email"
        />
      </div>
      
      <div class="form-group">
        <label for="password">Пароль</label>
        <input
          id="password"
          v-model="password"
          type="password"
          required
          placeholder="Введите пароль"
        />
      </div>

      <div class="form-group">
        <label for="confirmPassword">Подтвердите пароль</label>
        <input
          id="confirmPassword"
          v-model="confirmPassword"
          type="password"
          required
          placeholder="Повторите пароль"
        />
      </div>

      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="success" class="success-message">{{ success }}</div>
      
      <button type="submit" class="btn btn-primary" :disabled="loading">
        {{ loading ? 'Регистрация...' : 'Зарегистрироваться' }}
      </button>
    </form>
    
    <p class="auth-link">
      Уже есть аккаунт? <router-link to="/login">Войти</router-link>
    </p>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'RegisterForm',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const error = ref('')
    const success = ref('')
    const loading = ref(false)

    const handleRegister = async () => {
      error.value = ''
      success.value = ''

      if (password.value !== confirmPassword.value) {
        error.value = 'Пароли не совпадают'
        return
      }

      loading.value = true

      try {
        const result = await authStore.register(
          username.value,
          email.value,
          password.value
        )
        
        if (result.success) {
          success.value = result.message
          setTimeout(() => {
            router.push('/login')
          }, 2000)
        } else {
          error.value = result.message
        }
      } catch (err) {
        error.value = 'Произошла ошибка при регистрации'
      } finally {
        loading.value = false
      }
    }

    return {
      username,
      email,
      password,
      confirmPassword,
      error,
      success,
      loading,
      handleRegister
    }
  }
}
</script>

<style scoped>
.auth-form {
  max-width: 400px;
  margin: 50px auto;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.auth-form h2 {
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #4CAF50;
}

.error-message {
  color: #f44336;
  margin-bottom: 15px;
  padding: 10px;
  background: #ffebee;
  border-radius: 4px;
  font-size: 14px;
}

.success-message {
  color: #4CAF50;
  margin-bottom: 15px;
  padding: 10px;
  background: #e8f5e9;
  border-radius: 4px;
  font-size: 14px;
}

.btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #45a049;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.auth-link a {
  color: #4CAF50;
  text-decoration: none;
}

.auth-link a:hover {
  text-decoration: underline;
}
</style>