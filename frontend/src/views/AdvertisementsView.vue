<template>
  <div class="container">
    <h1>Объявления</h1>

    <div class="filters card">
      <div class="form-group">
        <label>Сортировка</label>
        <select v-model="sortBy" @change="loadAdvertisements">
          <option value="createdAt">По дате создания</option>
          <option value="price">По цене</option>
          <option value="title">По названию</option>
        </select>
      </div>
    </div>

    <div v-if="adsStore.loading" class="loading">
      Загрузка объявлений...
    </div>

    <div v-else-if="adsStore.error" class="error">
      {{ adsStore.error }}
    </div>

    <div v-else-if="adsStore.advertisements.length === 0" class="empty">
      <p>Объявлений пока нет</p>
      <router-link v-if="authStore.isAuthenticated" to="/advertisements/create" class="btn btn-primary">
        Создать первое объявление
      </router-link>
    </div>

    <div v-else class="ads-grid">
      <div v-for="ad in adsStore.advertisements" :key="ad.id" class="ad-card card">
        <div class="ad-header">
          <h3>{{ ad.title }}</h3>
          <span class="ad-type">{{ getTypeLabel(ad.type) }}</span>
        </div>
        
        <p class="ad-description">{{ ad.description }}</p>
        
        <div class="ad-footer">
          <div class="ad-price">{{ ad.price }} ₽</div>
          <div class="ad-meta">
            <span>Продавец: {{ ad.username }}</span>
            <span>{{ formatDate(ad.createdAt) }}</span>
          </div>
        </div>

        <div v-if="authStore.userId === ad.userId" class="ad-actions">
          <router-link :to="`/advertisements/${ad.id}/edit`" class="btn btn-secondary">
            Редактировать
          </router-link>
          <button @click="handleDelete(ad.id)" class="btn btn-danger">
            Удалить
          </button>
        </div>
      </div>
    </div>

    <div v-if="adsStore.totalPages > 1" class="pagination">
      <button 
        @click="changePage(adsStore.currentPage - 1)"
        :disabled="adsStore.currentPage === 0"
        class="btn btn-secondary"
      >
        Назад
      </button>
      
      <span class="page-info">
        Страница {{ adsStore.currentPage + 1 }} из {{ adsStore.totalPages }}
      </span>
      
      <button 
        @click="changePage(adsStore.currentPage + 1)"
        :disabled="adsStore.currentPage >= adsStore.totalPages - 1"
        class="btn btn-secondary"
      >
        Вперед
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAdvertisementsStore } from '@/stores/advertisements'

const authStore = useAuthStore()
const adsStore = useAdvertisementsStore()

const sortBy = ref('createdAt')

function getTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    'SALE': 'Продажа',
    'RENT': 'Аренда',
    'FAMILY_SHARE': 'Семейный доступ'
  }
  return labels[type] || type
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('ru-RU')
}

async function loadAdvertisements() {
  await adsStore.fetchAdvertisements({
    page: adsStore.currentPage,
    size: adsStore.pageSize,
    sortBy: sortBy.value
  })
}

async function changePage(page: number) {
  await adsStore.fetchAdvertisements({
    page,
    size: adsStore.pageSize,
    sortBy: sortBy.value
  })
}

async function handleDelete(adId: string) {
  if (!confirm('Вы уверены, что хотите удалить это объявление?')) {
    return
  }

  if (authStore.username && authStore.accessToken) {
    const result = await adsStore.deleteAdvertisement({
      username: authStore.username,
      token: authStore.accessToken,
      advertisementId: adId
    })

    if (result.success) {
      alert('Объявление успешно удалено')
    } else {
      alert(result.message || 'Ошибка удаления объявления')
    }
  }
}

onMounted(() => {
  loadAdvertisements()
})
</script>

<style scoped>
h1 {
  margin-bottom: 2rem;
}

.filters {
  margin-bottom: 2rem;
}

.ads-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.ad-card {
  display: flex;
  flex-direction: column;
  transition: transform 0.3s;
}

.ad-card:hover {
  transform: translateY(-5px);
}

.ad-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 1rem;
}

.ad-header h3 {
  margin: 0;
  color: var(--primary-color);
}

.ad-type {
  background: var(--secondary-color);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.ad-description {
  flex: 1;
  color: #666;
  margin-bottom: 1rem;
}

.ad-footer {
  border-top: 1px solid var(--border-color);
  padding-top: 1rem;
}

.ad-price {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.ad-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;
  color: #666;
}

.ad-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

.ad-actions .btn {
  flex: 1;
  padding: 8px;
  font-size: 0.875rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-info {
  font-weight: 500;
}

.empty {
  text-align: center;
  padding: 3rem;
}

.empty p {
  font-size: 1.25rem;
  color: #666;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .ads-grid {
    grid-template-columns: 1fr;
  }
}
</style>