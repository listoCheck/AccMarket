<template>
  <div id="app">
    <header class="header">
      <div class="container">
        <nav class="nav">
          <router-link to="/" class="logo">AccMarket</router-link>
          
          <div class="nav-links">
            <router-link to="/advertisements">Объявления</router-link>
            
            <template v-if="authStore.isAuthenticated">
              <router-link to="/advertisements/create">Создать объявление</router-link>
              <router-link to="/balance">Баланс</router-link>
              <router-link to="/notifications">
                Уведомления
                <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
              </router-link>
              <router-link to="/profile">Профиль</router-link>
              <button @click="handleLogout" class="btn btn-secondary">Выход</button>
            </template>
            
            <template v-else>
              <router-link to="/login">Вход</router-link>
              <router-link to="/register">Регистрация</router-link>
            </template>
          </div>
        </nav>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 AccMarket - Биржа игровых аккаунтов</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/api/client'

const router = useRouter()
const authStore = useAuthStore()
const unreadCount = ref(0)

async function loadUnreadNotifications() {
  if (authStore.isAuthenticated && authStore.userId) {
    try {
      const response = await apiClient.getUnreadNotifications(authStore.userId)
      if (response.data) {
        unreadCount.value = response.data.length || 0
      }
    } catch (error) {
      console.error('Ошибка загрузки уведомлений:', error)
    }
  }
}

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadUnreadNotifications()
  // Обновляем счетчик каждые 30 секунд
  setInterval(loadUnreadNotifications, 30000)
})
</script>

<style scoped>
.header {
  background: var(--card-bg);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 1rem 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--primary-color);
  text-decoration: none;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-links a {
  text-decoration: none;
  color: var(--text-color);
  transition: color 0.3s;
  position: relative;
}

.nav-links a:hover {
  color: var(--primary-color);
}

.nav-links a.router-link-active {
  color: var(--primary-color);
  font-weight: 500;
}

.badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: var(--danger-color);
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: bold;
}

.main {
  min-height: calc(100vh - 200px);
  padding: 2rem 0;
}

.footer {
  background: var(--card-bg);
  padding: 2rem 0;
  text-align: center;
  margin-top: 2rem;
  border-top: 1px solid var(--border-color);
}

.footer p {
  color: #666;
  margin: 0;
}

@media (max-width: 768px) {
  .nav {
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>