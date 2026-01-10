<template>
  <div class="moderation">
    <h1>Модерация объявлений</h1>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="success" class="alert alert-success">
      {{ success }}
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="advertisements.length === 0" class="empty-state">
      <p>Нет объявлений на модерации</p>
    </div>

    <div v-else class="advertisements-list">
      <div
        v-for="ad in advertisements"
        :key="ad.id"
        class="ad-card"
      >
        <div class="ad-content">
          <h3>{{ ad.title }}</h3>
          <p class="ad-description">{{ ad.description }}</p>
          <div class="ad-details">
            <span class="ad-price">{{ ad.price }} ₽</span>
            <span class="ad-type">{{ ad.type }}</span>
          </div>
          <div class="ad-meta">
            <span>Автор: {{ ad.authorUsername }}</span>
            <span>{{ formatDate(ad.createdAt) }}</span>
          </div>
        </div>

        <div class="ad-actions">
          <button
            @click="moderateAd(ad, 'APPROVED')"
            class="btn btn-success btn-sm"
          >
            Одобрить
          </button>
          <button
            @click="moderateAd(ad, 'REJECTED')"
            class="btn btn-danger btn-sm"
          >
            Отклонить
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
import { ref, onMounted } from 'vue'
import { adminAPI, moderationAPI } from '../../api/admin'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'Moderation',
  setup() {
    const authStore = useAuthStore()
    const advertisements = ref([])
    const loading = ref(false)
    const error = ref('')
    const success = ref('')
    const pagination = ref({
      page: 0,
      size: 10,
      totalPages: 0,
      totalElements: 0
    })

    const fetchAdvertisements = async (page = 0) => {
      loading.value = true
      error.value = ''

      try {
        const response = await adminAPI.getAdvertisements({
          page,
          size: 10,
          sortBy: 'createdAt'
        })

        advertisements.value = response.data.content || []
        pagination.value = {
          page: response.data.number,
          size: response.data.size,
          totalPages: response.data.totalPages,
          totalElements: response.data.totalElements
        }
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка загрузки объявлений'
      } finally {
        loading.value = false
      }
    }

    const moderateAd = async (ad, decision) => {
      error.value = ''
      success.value = ''

      const reason = decision === 'REJECTED' 
        ? prompt('Укажите причину отклонения:')
        : ''

      if (decision === 'REJECTED' && !reason) {
        return
      }

      try {
        const response = await moderationAPI.moderate({
          username: authStore.username,
          token: authStore.accessToken,
          advertisementId: ad.id,
          decision,
          reason
        })

        if (response.data.success) {
          success.value = `Объявление ${decision === 'APPROVED' ? 'одобрено' : 'отклонено'}`
          await fetchAdvertisements(pagination.value.page)
        } else {
          error.value = response.data.message || 'Ошибка модерации'
        }
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка модерации'
      }
    }

    const changePage = (page) => {
      fetchAdvertisements(page)
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
      advertisements,
      loading,
      error,
      success,
      pagination,
      moderateAd,
      changePage,
      formatDate
    }
  }
}
</script>

<style scoped>
.moderation {
  padding: 2rem 0;
}

.moderation h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
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

.advertisements-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.ad-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.ad-content {
  flex: 1;
}

.ad-content h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.ad-description {
  color: #7f8c8d;
  margin-bottom: 1rem;
}

.ad-details {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-bottom: 0.5rem;
}

.ad-price {
  font-size: 1.25rem;
  font-weight: bold;
  color: #27ae60;
}

.ad-type {
  background: #3498db;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
}

.ad-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: #95a5a6;
}

.ad-actions {
  display: flex;
  gap: 0.5rem;
  flex-direction: column;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  white-space: nowrap;
}

.btn-success {
  background: #27ae60;
  color: white;
}

.btn-success:hover {
  background: #229954;
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

.alert-success {
  background: #efe;
  color: #3c3;
  border: 1px solid #cfc;
}
</style>