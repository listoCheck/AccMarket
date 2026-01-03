<template>
  <div class="container">
    <div class="notifications-header">
      <h1>Уведомления</h1>
      <button 
        v-if="notifications.length > 0"
        @click="markAllAsRead" 
        class="btn btn-secondary"
        :disabled="processing"
      >
        Отметить все как прочитанные
      </button>
    </div>

    <div v-if="loading" class="loading">Загрузка уведомлений...</div>

    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else-if="notifications.length === 0" class="empty">
      <p>У вас пока нет уведомлений</p>
    </div>

    <div v-else class="notifications-list">
      <div 
        v-for="notification in notifications" 
        :key="notification.id"
        :class="['notification-card', 'card', { unread: !notification.isRead }]"
      >
        <div class="notification-header">
          <span class="notification-type">{{ getTypeLabel(notification.type) }}</span>
          <span class="notification-date">{{ formatDate(notification.createdAt) }}</span>
        </div>
        
        <p class="notification-text">{{ notification.text }}</p>
        
        <button 
          v-if="!notification.isRead"
          @click="markAsRead(notification.id)"
          class="btn btn-primary btn-sm"
        >
          Отметить как прочитанное
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/api/client'

interface Notification {
  id: string
  text: string
  type: string
  createdAt: string
  isRead: boolean
}

const authStore = useAuthStore()

const notifications = ref<Notification[]>([])
const loading = ref(true)
const processing = ref(false)
const error = ref('')

function getTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    'INFO': 'Информация',
    'SUCCESS': 'Успех',
    'WARNING': 'Предупреждение',
    'ERROR': 'Ошибка'
  }
  return labels[type] || type
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleString('ru-RU')
}

async function loadNotifications() {
  if (!authStore.userId) return

  loading.value = true
  error.value = ''

  try {
    const response = await apiClient.getNotifications(authStore.userId)
    if (response.data) {
      notifications.value = response.data
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Ошибка загрузки уведомлений'
  } finally {
    loading.value = false
  }
}

async function markAsRead(notificationId: string) {
  processing.value = true

  try {
    await apiClient.markNotificationAsRead(notificationId)
    
    // Обновляем локальное состояние
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.isRead = true
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Ошибка обновления уведомления'
  } finally {
    processing.value = false
  }
}

async function markAllAsRead() {
  if (!authStore.userId) return

  processing.value = true

  try {
    await apiClient.markAllNotificationsAsRead(authStore.userId)
    
    // Обновляем локальное состояние
    notifications.value.forEach(n => {
      n.isRead = true
    })
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Ошибка обновления уведомлений'
  } finally {
    processing.value = false
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

h1 {
  margin: 0;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.notification-card {
  transition: transform 0.3s;
}

.notification-card.unread {
  border-left: 4px solid var(--primary-color);
  background: #f0f8ff;
}

.notification-card:hover {
  transform: translateX(5px);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.notification-type {
  background: var(--secondary-color);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.notification-date {
  color: #666;
  font-size: 0.875rem;
}

.notification-text {
  margin: 1rem 0;
  color: var(--text-color);
}

.btn-sm {
  padding: 6px 12px;
  font-size: 0.875rem;
}

.empty {
  text-align: center;
  padding: 3rem;
}

.empty p {
  font-size: 1.25rem;
  color: #666;
}

@media (max-width: 768px) {
  .notifications-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
}
</style>