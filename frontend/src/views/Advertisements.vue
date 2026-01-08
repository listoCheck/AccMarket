<template>
  <div class="advertisements">
    <h1>Объявления</h1>

    <div class="filters">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Поиск по объявлениям..."
        class="search-input"
      />
      <select v-model="sortBy" class="sort-select">
        <option value="createdAt">По дате создания</option>
        <option value="price">По цене</option>
        <option value="title">По названию</option>
      </select>
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-else-if="advertisements.length === 0" class="empty-state">
      <p>Объявлений пока нет</p>
    </div>

    <div v-else class="advertisements-grid">
      <div
        v-for="ad in filteredAdvertisements"
        :key="ad.id"
        class="ad-card"
      >
        <h3>{{ ad.title }}</h3>
        <p class="ad-description">{{ ad.text }}</p>
        <div class="ad-details">
          <span class="ad-price">{{ ad.cost }} ₽</span>
          <span class="ad-platform">{{ ad.platform }}</span>
          <span class="ad-genre">{{ ad.genre }}</span>
        </div>
        <div class="ad-meta">
          <span>Автор: {{ ad.authorUsername }}</span>
          <span>{{ formatDate(ad.createdAt) }}</span>
        </div>
        <div class="ad-actions">
          <button
            v-if="authStore.userId === ad.authorId"
            @click="editAdvertisement(ad)"
            class="btn btn-secondary btn-sm"
          >
            Редактировать
          </button>
          <button
            v-if="authStore.userId === ad.authorId"
            @click="deleteAdvertisement(ad)"
            class="btn btn-danger btn-sm"
          >
            Удалить
          </button>
        </div>
      </div>
    </div>

    <div v-if="pagination.totalPages > 1" class="pagination">
      <button
        @click="changePage(pagination.page - 1)"
        :disabled="pagination.page === 0"
        class="btn btn-secondary"
      >
        Назад
      </button>
      <span>Страница {{ pagination.page + 1 }} из {{ pagination.totalPages }}</span>
      <button
        @click="changePage(pagination.page + 1)"
        :disabled="pagination.page >= pagination.totalPages - 1"
        class="btn btn-secondary"
      >
        Вперед
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdvertisementsStore } from '../stores/advertisements'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Advertisements',
  setup() {
    const router = useRouter()
    const adsStore = useAdvertisementsStore()
    const authStore = useAuthStore()
    
    const searchQuery = ref('')
    const sortBy = ref('createdAt')

    const advertisements = computed(() => adsStore.advertisements)
    const loading = computed(() => adsStore.loading)
    const error = computed(() => adsStore.error)
    const pagination = computed(() => adsStore.pagination)

    const filteredAdvertisements = computed(() => {
      let filtered = advertisements.value

      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(ad =>
          ad.title?.toLowerCase().includes(query) ||
          ad.text?.toLowerCase().includes(query) ||
          ad.platform?.toLowerCase().includes(query) ||
          ad.genre?.toLowerCase().includes(query)
        )
      }

      return filtered
    })

    const fetchAdvertisements = async (page = 0) => {
      await adsStore.fetchAdvertisements({
        page,
        size: 10,
        sortBy: sortBy.value
      })
    }

    const changePage = (page) => {
      fetchAdvertisements(page)
    }

    const editAdvertisement = (ad) => {
      adsStore.setCurrentAdvertisement(ad)
      router.push(`/advertisements/${ad.id}/edit`)
    }

    const deleteAdvertisement = async (ad) => {
      if (confirm('Вы уверены, что хотите удалить это объявление?')) {
        const result = await adsStore.deleteAdvertisement({
          username: authStore.username,
          token: authStore.accessToken,
          advertisementId: ad.id
        })

        if (result.success) {
          alert('Объявление успешно удалено')
          fetchAdvertisements()
        } else {
          alert(result.message || 'Ошибка удаления объявления')
        }
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ru-RU')
    }

    onMounted(() => {
      fetchAdvertisements()
    })

    return {
      searchQuery,
      sortBy,
      advertisements,
      filteredAdvertisements,
      loading,
      error,
      pagination,
      authStore,
      changePage,
      editAdvertisement,
      deleteAdvertisement,
      formatDate
    }
  }
}
</script>

<style scoped>
.advertisements {
  padding: 2rem 0;
}

.advertisements h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.search-input,
.sort-select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-input {
  flex: 1;
  min-width: 250px;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
}

.advertisements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.ad-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.ad-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.ad-card h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.ad-description {
  color: #7f8c8d;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ad-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.ad-price {
  font-size: 1.25rem;
  font-weight: bold;
  color: #27ae60;
}

.ad-platform,
.ad-genre {
  background: #3498db;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
  margin-left: 0.5rem;
}

.ad-genre {
  background: #9b59b6;
}

.ad-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;
  color: #95a5a6;
  margin-bottom: 1rem;
}

.ad-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
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