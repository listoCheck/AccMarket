<template>
  <div id="app">
    <header class="app-header">
      <div class="container">
        <router-link to="/" class="logo">
          <h1>AccMarket</h1>
        </router-link>
        
        <nav class="nav-menu">
          <router-link to="/" class="nav-link">Объявления</router-link>
          <router-link v-if="hasAdminAccess" to="/admin" class="nav-link">Админ</router-link>
          
          <div v-if="isAuthenticated" class="user-menu">
            <span class="username">{{ username }}</span>
            <button @click="handleLogout" class="btn btn-logout">Выход</button>
          </div>
          <div v-else class="auth-links">
            <router-link to="/login" class="btn btn-login">Вход</router-link>
            <router-link to="/register" class="btn btn-register">Регистрация</router-link>
          </div>
        </nav>
      </div>
    </header>

    <main class="app-main">
      <router-view />
    </main>

    <footer class="app-footer">
      <div class="container">
        <p>&copy; 2024 AccMarket - Маркетплейс игровых аккаунтов</p>
        <p class="footer-note">
          ⚠️ Функции пополнения баланса и покупки аккаунтов находятся в разработке
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()

    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const username = computed(() => authStore.username)
    const hasAdminAccess = computed(() => authStore.hasAdminAccess)

    const handleLogout = async () => {
      await authStore.logout()
      router.push('/login')
    }

    return {
      isAuthenticated,
      username,
      hasAdminAccess,
      handleLogout
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-header {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.app-header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
}

.logo {
  text-decoration: none;
  color: #4CAF50;
}

.logo h1 {
  font-size: 24px;
  font-weight: bold;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-link {
  text-decoration: none;
  color: #666;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #4CAF50;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #333;
  font-weight: 500;
}

.auth-links {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s;
}

.btn-login {
  background: transparent;
  color: #4CAF50;
  border: 1px solid #4CAF50;
}

.btn-login:hover {
  background: #4CAF50;
  color: white;
}

.btn-register {
  background: #4CAF50;
  color: white;
}

.btn-register:hover {
  background: #45a049;
}

.btn-logout {
  background: #f44336;
  color: white;
}

.btn-logout:hover {
  background: #da190b;
}

.app-main {
  flex: 1;
  padding: 20px 0;
}

.app-footer {
  background: #333;
  color: white;
  padding: 20px 0;
  margin-top: 40px;
}

.app-footer .container {
  text-align: center;
}

.app-footer p {
  margin: 5px 0;
}

.footer-note {
  color: #ffc107;
  font-size: 14px;
  margin-top: 10px;
}
</style>