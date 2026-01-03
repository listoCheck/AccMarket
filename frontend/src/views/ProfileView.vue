<template>
  <div class="container">
    <h1>Профиль</h1>

    <div class="profile-grid">
      <div class="profile-info card">
        <h2>Информация о пользователе</h2>
        
        <div class="info-item">
          <span class="label">Логин:</span>
          <span class="value">{{ authStore.username }}</span>
        </div>

        <div class="info-item">
          <span class="label">Email:</span>
          <span class="value">{{ authStore.email }}</span>
        </div>

        <div class="info-item">
          <span class="label">ID:</span>
          <span class="value">{{ authStore.userId }}</span>
        </div>
      </div>

      <div class="user-ads card">
        <h2>Мои объявления</h2>
        
        <div v-if="loading" class="loading">Загрузка...</div>
        
        <div v-else-if="userAds.length === 0" class="empty">
          <p>У вас пока нет объявлений</p>
          <router-link to="/advertisements/create" class="btn btn-primary">
            Создать объявление
          </router-link>
        </div>

        <div v-else class="ads-list">
          <div v-for="ad in userAds" :key="ad.id" class="ad-item">
            <div class="ad-info">
              <h3>{{ ad.title }}</h3>
              <span class="ad-price">{{ ad.price }} ₽</span>
            </div>
            <div class="ad-actions">
              <router-link :to="`/advertisements/${ad.id}/edit`" class="btn btn-secondary btn-sm">
                Редактировать
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAdvertisementsStore } from '@/stores/advertisements'
import type { Advertisement } from '@/stores/advertisements'

const authStore = useAuthStore()
const adsStore = useAdvertisementsStore()

const userAds = ref<Advertisement[]>([])
const loading = ref(true)

async function loadUserAds() {
  if (!authStore.userId) return

  loading.value = true

  try {
    await adsStore.fetchUserAdvertisements(authStore.userId)
    userAds.value = adsStore.advertisements
  } catch (error) {
    console.error('Ошибка загрузки объявлений:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserAds()
})
</script>

<style scoped>
h1 {
  margin-bottom: 2rem;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 2rem;
}

.profile-info h2,
.user-ads h2 {
  margin-bottom: 1.5rem;
  color: var(--primary-color);
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 1rem 0;
  border-bottom: 1px solid var(--border-color);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 500;
  color: #666;
}

.info-item .value {
  color: var(--text-color);
}

.ads-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.ad-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: var(--bg-color);
  border-radius: 4px;
}

.ad-info h3 {
  margin: 0 0 0.5rem 0;
  color: var(--primary-color);
}

.ad-price {
  font-weight: bold;
  color: var(--text-color);
}

.ad-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 0.875rem;
}

.empty {
  text-align: center;
  padding: 2rem;
}

.empty p {
  margin-bottom: 1rem;
  color: #666;
}

@media (max-width: 768px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .ad-item {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }
}
</style>