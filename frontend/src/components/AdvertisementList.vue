<template>
  <div class="advertisement-list">
    <div class="list-header">
      <h2>Объявления</h2>
      <button v-if="isAuthenticated" @click="showCreateForm = true" class="btn btn-primary">
        Создать объявление
      </button>
    </div>

    <div class="filters">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Поиск по названию..."
        class="search-input"
      />
      <select v-model="sortBy" class="sort-select">
        <option value="createdAt">По дате создания</option>
        <option value="cost">По цене</option>
        <option value="title">По названию</option>
      </select>
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>
    
    <div v-else-if="error" class="error-message">{{ error }}</div>
    
    <div v-else-if="filteredAdvertisements.length === 0" class="no-data">
      Объявлений не найдено
    </div>
    
    <div v-else class="ads-grid">
      <div
        v-for="ad in filteredAdvertisements"
        :key="ad.id"
        class="ad-card"
      >
        <div class="ad-header">
          <h3>{{ ad.title }}</h3>
          <span class="ad-price">{{ ad.cost }} ₽</span>
        </div>
        <p class="ad-description">{{ ad.text }}</p>
        <div class="ad-meta">
          <span v-if="ad.platform" class="ad-tag">{{ ad.platform }}</span>
          <span v-if="ad.genre" class="ad-tag">{{ ad.genre }}</span>
        </div>
        <div v-if="canEdit(ad)" class="ad-actions">
          <button @click="editAdvertisement(ad)" class="btn btn-small btn-secondary">
            Редактировать
          </button>
          <button @click="deleteAdvertisement(ad.id)" class="btn btn-small btn-danger">
            Удалить
          </button>
        </div>
        <div v-else class="ad-actions">
          <button class="btn btn-small btn-disabled" disabled>
            Купить (скоро)
          </button>
        </div>
      </div>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <button
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 0"
        class="btn btn-small"
      >
        Назад
      </button>
      <span class="page-info">Страница {{ currentPage + 1 }} из {{ totalPages }}</span>
      <button
        @click="changePage(currentPage + 1)"
        :disabled="currentPage >= totalPages - 1"
        class="btn btn-small"
      >
        Вперед
      </button>
    </div>

    <AdvertisementForm
      v-if="showCreateForm"
      :advertisement="editingAd"
      @close="closeForm"
      @saved="loadAdvertisements"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'
import AdvertisementForm from './AdvertisementForm.vue'

export default {
  name: 'AdvertisementList',
  components: {
    AdvertisementForm
  },
  setup() {
    const authStore = useAuthStore()
    const advertisements = ref([])
    const loading = ref(false)
    const error = ref('')
    const searchQuery = ref('')
    const sortBy = ref('createdAt')
    const currentPage = ref(0)
    const totalPages = ref(0)
    const pageSize = ref(10)
    const showCreateForm = ref(false)
    const editingAd = ref(null)

    const isAuthenticated = computed(() => authStore.isAuthenticated)

    const filteredAdvertisements = computed(() => {
      let filtered = advertisements.value

      if (searchQuery.value) {
        filtered = filtered.filter(ad =>
          ad.title.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      return filtered
    })

    const loadAdvertisements = async () => {
      loading.value = true
      error.value = ''

      try {
        const response = await api.advertisements.getAll({
          page: currentPage.value,
          size: pageSize.value,
          sortBy: sortBy.value
        })

        if (response.data) {
          advertisements.value = response.data.content || []
          totalPages.value = response.data.totalPages || 0
        }
      } catch (err) {
        error.value = 'Ошибка загрузки объявлений'
        console.error(err)
      } finally {
        loading.value = false
      }
    }

    const changePage = (page) => {
      currentPage.value = page
      loadAdvertisements()
    }

    const canEdit = (ad) => {
      return isAuthenticated.value && authStore.username
    }

    const editAdvertisement = (ad) => {
      editingAd.value = ad
      showCreateForm.value = true
    }

    const deleteAdvertisement = async (adId) => {
      if (!confirm('Вы уверены, что хотите удалить это объявление?')) {
        return
      }

      try {
        await api.advertisements.delete({
          username: authStore.username,
          token: authStore.accessToken,
          advertisementId: adId
        })
        loadAdvertisements()
      } catch (err) {
        alert('Ошибка удаления объявления')
        console.error(err)
      }
    }

    const closeForm = () => {
      showCreateForm.value = false
      editingAd.value = null
    }

    onMounted(() => {
      loadAdvertisements()
    })

    return {
      advertisements,
      loading,
      error,
      searchQuery,
      sortBy,
      currentPage,
      totalPages,
      showCreateForm,
      editingAd,
      isAuthenticated,
      filteredAdvertisements,
      changePage,
      canEdit,
      editAdvertisement,
      deleteAdvertisement,
      closeForm,
      loadAdvertisements
    }
  }
}
</script>

<style scoped>
.advertisement-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.list-header h2 {
  color: #333;
  margin: 0;
}

.filters {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.search-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.sort-select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
}

.error-message {
  color: #f44336;
  padding: 15px;
  background: #ffebee;
  border-radius: 4px;
  margin-bottom: 20px;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #999;
}

.ads-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.ad-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  transition: box-shadow 0.3s;
}

.ad-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.ad-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 15px;
}

.ad-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  flex: 1;
}

.ad-price {
  font-size: 20px;
  font-weight: bold;
  color: #4CAF50;
  white-space: nowrap;
  margin-left: 10px;
}

.ad-description {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.5;
}

.ad-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.ad-tag {
  display: inline-block;
  padding: 4px 12px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 12px;
  font-size: 12px;
}

.ad-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.page-info {
  color: #666;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #2196F3;
  color: white;
}

.btn-secondary:hover {
  background-color: #0b7dda;
}

.btn-danger {
  background-color: #f44336;
  color: white;
}

.btn-danger:hover {
  background-color: #da190b;
}

.btn-disabled {
  background-color: #ccc;
  color: #666;
  cursor: not-allowed;
}

.btn-small {
  padding: 8px 16px;
  font-size: 13px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>