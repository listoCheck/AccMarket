<template>
  <header class="header">
    <div class="header-container">
      <router-link to="/" class="logo">
        <h1>AccMarket</h1>
      </router-link>
      
      <nav class="nav">
        <router-link to="/advertisements" class="nav-link">Объявления</router-link>
        
        <template v-if="authStore.isAuthenticated">
          <router-link to="/advertisements/create" class="nav-link">Создать объявление</router-link>
          <router-link to="/notifications" class="nav-link">
            Уведомления
            <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
          </router-link>
          
          <router-link v-if="authStore.isModerator" to="/admin/moderation" class="nav-link">
            Модерация
          </router-link>
          
          <router-link v-if="authStore.isAdmin" to="/admin" class="nav-link">
            Админ-панель
          </router-link>
          
          <div class="user-info">
            <router-link to="/profile" class="username-link">
              {{ authStore.username }}
            </router-link>
            <router-link to="/balance" class="balance-link">
              {{ balance }} ₽
            </router-link>
            <button @click="handleLogout" class="btn btn-secondary">Выход</button>
          </div>
        </template>
        
        <template v-else>
          <router-link to="/login" class="nav-link">Вход</router-link>
          <router-link to="/register" class="btn btn-primary">Регистрация</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { ref, onMounted, watch } from 'vue'
import { notificationsAPI } from '../api/notifications'
import { balanceAPI } from '../api/balance'

export default {
  name: 'Header',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const unreadCount = ref(0)
    const balance = ref(0)

    const handleLogout = async () => {
      await authStore.logout()
      router.push('/login')
    }

    const fetchUnreadCount = async () => {
      if (authStore.isAuthenticated && authStore.userId) {
        try {
          const response = await notificationsAPI.getUnread(authStore.userId)
          unreadCount.value = response.data.length || 0
        } catch (error) {
          console.error('Error fetching unread notifications:', error)
        }
      }
    }

    const fetchBalance = async () => {
      if (authStore.isAuthenticated && authStore.userId) {
        try {
          const response = await balanceAPI.get(authStore.userId)
          if (response.data) {
            balance.value = response.data.amount || 0
          }
        } catch (error) {
          console.error('Error fetching balance:', error)
          balance.value = 0
        }
      }
    }

    onMounted(() => {
      fetchUnreadCount()
      fetchBalance()
      // Poll for new notifications and balance every 30 seconds
      setInterval(() => {
        fetchUnreadCount()
        fetchBalance()
      }, 30000)
      
      // Слушаем событие обновления баланса
      window.addEventListener('balance-updated', fetchBalance)
    })

    // Watch for authentication changes
    watch(() => authStore.isAuthenticated, (newVal) => {
      if (newVal) {
        fetchBalance()
      } else {
        balance.value = 0
      }
    })

    return {
      authStore,
      unreadCount,
      balance,
      handleLogout
    }
  }
}
</script>

<style scoped>
.header {
  background: #2c3e50;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  text-decoration: none;
  color: white;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
}

.nav {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
  position: relative;
}

.nav-link:hover {
  background: rgba(255,255,255,0.1);
}

.nav-link.router-link-active {
  background: rgba(255,255,255,0.2);
}

.badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #e74c3c;
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 0.75rem;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding-left: 1rem;
  border-left: 1px solid rgba(255,255,255,0.2);
}

.username-link {
  color: white;
  text-decoration: none;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.username-link:hover {
  background: rgba(255,255,255,0.1);
}

.balance-link {
  color: #2ecc71;
  font-weight: 600;
  font-size: 0.95rem;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.balance-link:hover {
  background: rgba(46, 204, 113, 0.1);
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover {
  background: #2980b9;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
}
</style>