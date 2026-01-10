<template>
  <div class="profile">
    <h1>Профиль пользователя</h1>

    <div class="profile-card">
      <div class="profile-info">
        <h2>{{ authStore.username }}</h2>
        <p class="user-id">ID: {{ authStore.userId }}</p>
        <div class="roles">
          <span
            v-for="role in authStore.roles"
            :key="role"
            class="role-badge"
          >
            {{ role }}
          </span>
        </div>
      </div>
    </div>

    <div class="user-advertisements">
      <h2>Мои объявления</h2>

      <div v-if="loading" class="loading">Загрузка...</div>

      <div v-else-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-else-if="advertisements.length === 0" class="empty-state">
        <p>У вас пока нет объявлений</p>
        <router-link to="/advertisements/create" class="btn btn-primary">
          Создать первое объявление
        </router-link>
      </div>

      <div v-else class="advertisements-list">
        <div
          v-for="ad in advertisements"
          :key="ad.id"
          class="ad-item"
        >
          <div class="ad-content">
            <h3>{{ ad.title }}</h3>
            <p class="ad-description">{{ ad.text }}</p>
            <div class="ad-meta">
              <span class="ad-price">{{ ad.cost }} ₽</span>
              <span class="ad-type" v-if="ad.platform">{{ ad.platform }}</span>
              <span class="ad-type genre" v-if="ad.genre">{{ ad.genre }}</span>
            </div>
          </div>
          <div class="ad-actions">
            <button
              @click="editAdvertisement(ad)"
              class="btn btn-secondary btn-sm"
            >
              Редактировать
            </button>
            <button
              @click="deleteAdvertisement(ad)"
              class="btn btn-danger btn-sm"
            >
              Удалить
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useAdvertisementsStore } from '../stores/advertisements'

export default {
  name: 'Profile',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const adsStore = useAdvertisementsStore()

    const loading = ref(false)
    const error = ref('')

    const advertisements = computed(() => adsStore.advertisements)

    const fetchUserAdvertisements = async () => {
      loading.value = true
      error.value = ''

      try {
        console.log('Fetching advertisements for user:', authStore.userId)
        const result = await adsStore.fetchUserAdvertisements(authStore.userId)
        console.log('Fetch result:', result)
        console.log('Advertisements in store:', adsStore.advertisements)
        if (!result.success) {
          error.value = result.message || 'Ошибка загрузки объявлений'
        }
      } catch (err) {
        console.error('Error fetching advertisements:', err)
        error.value = 'Произошла ошибка при загрузке объявлений'
      } finally {
        loading.value = false
      }
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
          // Store автоматически удалит объявление из списка
        } else {
          alert(result.message || 'Ошибка удаления объявления')
        }
      }
    }

    onMounted(() => {
      fetchUserAdvertisements()
    })

    return {
      authStore,
      loading,
      error,
      advertisements,
      editAdvertisement,
      deleteAdvertisement
    }
  }
}
</script>

<style scoped>
.profile {
  padding: 2rem 0;
}

.profile h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.profile-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 3rem;
}

.profile-info h2 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.user-id {
  color: #7f8c8d;
  margin-bottom: 1rem;
}

.roles {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.role-badge {
  background: #3498db;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
}

.user-advertisements h2 {
  color: #2c3e50;
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
}

.empty-state p {
  color: #7f8c8d;
  margin-bottom: 1rem;
}

.advertisements-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.ad-item {
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
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ad-meta {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
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

.ad-type.genre {
  background: #9b59b6;
}

.ad-date {
  color: #95a5a6;
  font-size: 0.875rem;
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

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
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