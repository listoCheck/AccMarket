<template>
  <div class="notifications">
    <h1>Уведомления</h1>

    <div class="notifications-header">
      <button
        v-if="unreadNotifications.length > 0"
        @click="markAllAsRead"
        class="btn btn-secondary"
      >
        Отметить все как прочитанные
      </button>
    </div>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="notifications.length === 0" class="empty-state">
      <p>У вас пока нет уведомлений</p>
    </div>

    <div v-else class="notifications-list">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        :class="['notification-card', { unread: !notification.isRead }]"
        @click="markAsRead(notification)"
      >
        <div class="notification-icon">
          {{ getNotificationIcon(notification.type) }}
        </div>
        <div class="notification-content">
          <h3>{{ notification.title }}</h3>
          <p>{{ notification.message }}</p>
          <span class="notification-date">{{ formatDate(notification.createdAt) }}</span>
        </div>
        <div v-if="!notification.isRead" class="unread-badge"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { notificationsAPI } from '../api/notifications'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Notifications',
  setup() {
    const authStore = useAuthStore()
    const notifications = ref([])
    const loading = ref(false)
    const error = ref('')

    const unreadNotifications = computed(() => 
      notifications.value.filter(n => !n.isRead)
    )

    const fetchNotifications = async () => {
      loading.value = true
      error.value = ''

      try {
        console.log('Fetching notifications for userId:', authStore.userId)
        const response = await notificationsAPI.getAll(authStore.userId)
        console.log('Notifications response:', response)
        console.log('Response data:', response.data)
        notifications.value = response.data || []
        console.log('Notifications loaded:', notifications.value.length)
      } catch (err) {
        console.error('Error fetching notifications:', err)
        console.error('Error response:', err.response)
        error.value = err.response?.data?.message || 'Ошибка загрузки уведомлений'
      } finally {
        loading.value = false
      }
    }

    const markAsRead = async (notification) => {
      if (notification.isRead) return

      try {
        await notificationsAPI.markAsRead(notification.id, authStore.userId)
        notification.isRead = true
      } catch (err) {
        console.error('Error marking notification as read:', err)
      }
    }

    const markAllAsRead = async () => {
      try {
        await notificationsAPI.markAllAsRead(authStore.userId)
        notifications.value.forEach(n => n.isRead = true)
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка отметки уведомлений'
      }
    }

    const getNotificationIcon = (type) => {
      const icons = {
        'INFO': 'ℹ️',
        'SUCCESS': '✅',
        'WARNING': '⚠️',
        'ERROR': '❌',
        'MESSAGE': '💬'
      }
      return icons[type] || 'ℹ️'
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return 'только что'
      if (minutes < 60) return `${minutes} мин. назад`
      if (hours < 24) return `${hours} ч. назад`
      if (days < 7) return `${days} дн. назад`
      return date.toLocaleDateString('ru-RU')
    }

    onMounted(() => {
      fetchNotifications()
    })

    return {
      notifications,
      unreadNotifications,
      loading,
      error,
      markAsRead,
      markAllAsRead,
      getNotificationIcon,
      formatDate
    }
  }
}
</script>

<style scoped>
.notifications {
  padding: 2rem 0;
}

.notifications h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.notifications-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1.5rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  color: #7f8c8d;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.notification-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
}

.notification-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.notification-card.unread {
  background: #f0f8ff;
  border-left: 4px solid #3498db;
}

.notification-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
}

.notification-content h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-size: 1.1rem;
}

.notification-content p {
  color: #7f8c8d;
  margin-bottom: 0.5rem;
}

.notification-date {
  font-size: 0.875rem;
  color: #95a5a6;
}

.unread-badge {
  width: 12px;
  height: 12px;
  background: #3498db;
  border-radius: 50%;
  flex-shrink: 0;
}

.alert {
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.alert-error {
  background: #fee;
  color: #c33;
  border: 1px solid #fcc;
}
</style>