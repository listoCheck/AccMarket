<template>
  <div class="container">
    <div class="auth-container">
      <div class="card">
        <h1>Регистрация</h1>
        
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username">Логин</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              placeholder="Введите логин"
            />
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              placeholder="Введите email"
            />
          </div>

          <div class="form-group">
            <label for="password">Пароль</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              placeholder="Введите пароль"
            />
          </div>

          <div class="form-group">
            <label for="confirmPassword">Подтвердите пароль</label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              placeholder="Повторите пароль"
            />
          </div>

          <div v-if="error" class="error">{{ error }}</div>
          <div v-if="success" class="success">{{ success }}</div>

          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Регистрация...' : 'Зарегистрироваться' }}
          </button>
        </form>

        <p class="auth-link">
          Уже есть аккаунт? <router-link to="/login">Войти</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')
const success = ref('')

async function handleRegister() {
  loading.value = true
  error.value = ''
  success.value = ''

  // Валидация
  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Пароли не совпадают'
    loading.value = false
    return
  }

  if (form.value.password.length < 6) {
    error.value = 'Пароль должен содержать минимум 6 символов'
    loading.value = false
    return
  }

  try {
    const result = await authStore.register({
      username: form.value.username,
      email: form.value.email,
      password: form.value.password
    })
    
    if (result.success) {
      success.value = 'Регистрация успешна! Перенаправление на страницу входа...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      error.value = result.message || 'Ошибка регистрации'
    }
  } catch (err: any) {
    error.value = err.message || 'Произошла ошибка'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.card {
  max-width: 400px;
  width: 100%;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--primary-color);
}

.btn {
  width: 100%;
  margin-top: 1rem;
}

.auth-link {
  text-align: center;
  margin-top: 1rem;
}

.auth-link a {
  color: var(--primary-color);
  text-decoration: none;
}

.auth-link a:hover {
  text-decoration: underline;
}
</style>