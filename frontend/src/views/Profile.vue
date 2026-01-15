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

    <!-- Tabs -->
    <div class="tabs">
      <button
        :class="['tab', { active: activeTab === 'my-ads' }]"
        @click="activeTab = 'my-ads'"
      >
        Мои объявления
      </button>
      <button
        :class="['tab', { active: activeTab === 'purchases' }]"
        @click="activeTab = 'purchases'"
      >
        Мои покупки
      </button>
    </div>

    <!-- Мои объявления -->
    <div v-show="activeTab === 'my-ads'" class="user-advertisements">
      <h2>Мои объявления</h2>

      <div v-if="loading" class="loading">Загрузка...</div>

      <div v-else-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-if="appealSuccess" class="alert alert-success">
        {{ appealSuccess }}
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
          :class="['ad-item', { 'rejected': ad.status === 'MODERATION_REJECTED' }]"
        >
          <div class="ad-content">
            <h3>{{ ad.title }}</h3>
            <p class="ad-description">{{ ad.text }}</p>
            <div class="ad-meta">
              <span class="ad-price">{{ ad.cost }} ₽</span>
              <span class="ad-type" v-if="ad.platform">{{ ad.platform }}</span>
              <span class="ad-type genre" v-if="ad.genre">{{ ad.genre }}</span>
            </div>
            
            <!-- Статус модерации -->
            <div v-if="ad.status === 'MODERATION_REJECTED'" class="moderation-info">
              <div class="status-badge rejected">
                <span class="status-icon">❌</span>
                Отклонено модерацией
              </div>
              <div v-if="ad.moderationComment" class="rejection-reason">
                <strong>Причина:</strong> {{ ad.moderationComment }}
              </div>
              <div v-else class="rejection-reason loading-reason">
                Загрузка причины...
              </div>
            </div>
            <div v-else-if="ad.status === 'MODERATION_PENDING'" class="moderation-info">
              <div class="status-badge pending">
                <span class="status-icon">⏳</span>
                На модерации
              </div>
            </div>
            <div v-else-if="ad.status === 'MODERATION_APPROVED'" class="moderation-info">
              <div class="status-badge approved">
                <span class="status-icon">✅</span>
                Одобрено
              </div>
            </div>
          </div>
          
          <div class="ad-actions">
            <template v-if="ad.status === 'MODERATION_REJECTED'">
              <button
                @click="openAppealModal(ad)"
                class="btn btn-primary btn-sm"
              >
                Подать апелляцию
              </button>
              <button
                @click="deleteAdvertisement(ad)"
                class="btn btn-danger btn-sm"
              >
                Удалить
              </button>
            </template>
            <template v-else>
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
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Мои покупки -->
    <div v-show="activeTab === 'purchases'" class="user-purchases">
      <h2>Мои покупки</h2>

      <div v-if="loadingPurchases" class="loading">Загрузка...</div>

      <div v-else-if="purchasesError" class="alert alert-error">
        {{ purchasesError }}
      </div>

      <div v-else-if="purchases.length === 0" class="empty-state">
        <p>У вас пока нет покупок</p>
        <router-link to="/advertisements" class="btn btn-primary">
          Перейти к объявлениям
        </router-link>
      </div>

      <div v-else class="purchases-list">
        <div
          v-for="purchase in purchases"
          :key="purchase.advertisementId"
          class="purchase-item"
        >
          <div class="purchase-content">
            <h3>{{ purchase.title }}</h3>
            <div class="account-credentials">
              <div class="credential-row">
                <span class="credential-label">Логин:</span>
                <span class="credential-value">{{ purchase.login }}</span>
                <button
                  @click="copyToClipboard(purchase.login)"
                  class="btn-copy"
                  title="Копировать"
                >
                  📋
                </button>
              </div>
              <div class="credential-row">
                <span class="credential-label">Пароль:</span>
                <span class="credential-value">{{ purchase.password }}</span>
                <button
                  @click="copyToClipboard(purchase.password)"
                  class="btn-copy"
                  title="Копировать"
                >
                  📋
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Модальное окно для создания апелляции -->
    <div v-if="showAppealModal" class="modal-overlay" @click="closeAppealModal">
      <div class="modal-content" @click.stop>
        <h3>Подать апелляцию</h3>
        <p class="modal-ad-title">Объявление: {{ selectedAd?.title }}</p>

        <div v-if="modalError" class="alert alert-error">
          {{ modalError }}
        </div>

        <form @submit.prevent="submitAppeal">
          <div class="form-group">
            <label for="reason">Причина апелляции</label>
            <textarea
              id="reason"
              v-model="appealReason"
              required
              rows="4"
              placeholder="Объясните, почему объявление должно быть одобрено"
            ></textarea>
          </div>

          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="modalLoading">
              {{ modalLoading ? 'Отправка...' : 'Отправить' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="closeAppealModal">
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useAdvertisementsStore } from '../stores/advertisements'
import { advertisementsAPI } from '../api/advertisements'
import { moderationAPI, appealsAPI } from '../api/admin'

export default {
  name: 'Profile',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    const adsStore = useAdvertisementsStore()

    const activeTab = ref('my-ads')
    const loading = ref(false)
    const error = ref('')
    const loadingPurchases = ref(false)
    const purchasesError = ref('')
    const purchases = ref([])
    const appealSuccess = ref('')

    const showAppealModal = ref(false)
    const selectedAd = ref(null)
    const appealReason = ref('')
    const modalLoading = ref(false)
    const modalError = ref('')

    const advertisements = computed(() => adsStore.advertisements)

    const fetchUserAdvertisements = async () => {
      loading.value = true
      error.value = ''

      try {
        console.log('Fetching advertisements for user:', authStore.userId)
        const result = await adsStore.fetchUserAdvertisements(authStore.userId)
        console.log('Fetch result:', result)
        console.log('Advertisements in store:', adsStore.advertisements)
        
        if (result.success) {
          // Загружаем информацию о модерации для отклоненных объявлений
          await fetchModerationInfo()
        } else {
          error.value = result.message || 'Ошибка загрузки объявлений'
        }
      } catch (err) {
        console.error('Error fetching advertisements:', err)
        error.value = 'Произошла ошибка при загрузке объявлений'
      } finally {
        loading.value = false
      }
    }

    const fetchModerationInfo = async () => {
      // Для каждого отклоненного объявления загружаем информацию о модерации
      const rejectedAds = adsStore.advertisements.filter(
        ad => ad.status === 'MODERATION_REJECTED'
      )

      for (const ad of rejectedAds) {
        try {
          const response = await moderationAPI.getByAdvertisement(ad.id)
          if (response.data) {
            // Добавляем комментарий модерации к объявлению
            ad.moderationComment = response.data.comment || 'Причина не указана'
          }
        } catch (err) {
          console.error(`Error fetching moderation for ad ${ad.id}:`, err)
          ad.moderationComment = 'Не удалось загрузить причину'
        }
      }
    }

    const fetchPurchases = async () => {
      loadingPurchases.value = true
      purchasesError.value = ''

      try {
        console.log('Fetching purchases...')
        const response = await advertisementsAPI.getBought()
        console.log('Purchases response:', response)
        purchases.value = response.data || []
        console.log('Purchases loaded:', purchases.value.length)
      } catch (err) {
        console.error('Error fetching purchases:', err)
        purchasesError.value = err.response?.data?.message || 'Ошибка загрузки покупок'
      } finally {
        loadingPurchases.value = false
      }
    }

    const copyToClipboard = async (text) => {
      try {
        await navigator.clipboard.writeText(text)
        alert('Скопировано в буфер обмена!')
      } catch (err) {
        console.error('Failed to copy:', err)
        // Fallback для старых браузеров
        const textArea = document.createElement('textarea')
        textArea.value = text
        document.body.appendChild(textArea)
        textArea.select()
        try {
          document.execCommand('copy')
          alert('Скопировано в буфер обмена!')
        } catch (err) {
          alert('Не удалось скопировать')
        }
        document.body.removeChild(textArea)
      }
    }

    const openAppealModal = (ad) => {
      selectedAd.value = ad
      appealReason.value = ''
      modalError.value = ''
      showAppealModal.value = true
    }

    const closeAppealModal = () => {
      showAppealModal.value = false
      selectedAd.value = null
      appealReason.value = ''
      modalError.value = ''
    }

    const submitAppeal = async () => {
      modalLoading.value = true
      modalError.value = ''

      try {
        const response = await appealsAPI.create({
          username: authStore.username,
          token: authStore.accessToken,
          advertisementId: selectedAd.value.id,
          reason: appealReason.value
        })

        if (response.status === 200 || response.data.success) {
          appealSuccess.value = 'Апелляция успешно создана и отправлена на рассмотрение'
          closeAppealModal()
          setTimeout(() => {
            appealSuccess.value = ''
          }, 5000)
        } else {
          modalError.value = response.data.message || 'Ошибка создания апелляции'
        }
      } catch (err) {
        modalError.value = err.response?.data?.message || 'Ошибка создания апелляции'
      } finally {
        modalLoading.value = false
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

    // Загружаем покупки при переключении на вкладку
    watch(activeTab, (newTab) => {
      if (newTab === 'purchases' && purchases.value.length === 0) {
        fetchPurchases()
      }
    })

    onMounted(() => {
      fetchUserAdvertisements()
    })

    return {
      authStore,
      activeTab,
      loading,
      error,
      advertisements,
      loadingPurchases,
      purchasesError,
      purchases,
      appealSuccess,
      showAppealModal,
      selectedAd,
      appealReason,
      modalLoading,
      modalError,
      openAppealModal,
      closeAppealModal,
      submitAppeal,
      editAdvertisement,
      deleteAdvertisement,
      copyToClipboard
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

.user-advertisements h2,
.user-purchases h2 {
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
  align-items: flex-start;
  gap: 1rem;
  transition: all 0.3s;
}

.ad-item.rejected {
  background: #f8f9fa;
  border-left: 4px solid #e74c3c;
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
  margin-bottom: 0.75rem;
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

.moderation-info {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #ecf0f1;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.status-badge.rejected {
  background: #fee;
  color: #e74c3c;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.approved {
  background: #d4edda;
  color: #155724;
}

.status-icon {
  font-size: 1rem;
}

.rejection-reason {
  background: #fff5f5;
  padding: 0.75rem;
  border-radius: 4px;
  color: #c0392b;
  font-size: 0.9rem;
  line-height: 1.5;
}

.rejection-reason.loading-reason {
  color: #7f8c8d;
  font-style: italic;
}

.rejection-reason strong {
  color: #e74c3c;
}

.ad-actions {
  display: flex;
  gap: 0.5rem;
  flex-direction: column;
  align-self: center;
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

.alert-success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

/* Purchases */
.purchases-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.purchase-item {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-left: 4px solid #27ae60;
}

.purchase-content h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
  font-size: 1.25rem;
}

.account-credentials {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.credential-row {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.credential-label {
  font-weight: 600;
  color: #2c3e50;
  min-width: 80px;
}

.credential-value {
  flex: 1;
  font-family: 'Courier New', monospace;
  background: white;
  padding: 0.5rem 0.75rem;
  border-radius: 4px;
  border: 1px solid #ddd;
  color: #2c3e50;
}

.btn-copy {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.5rem 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.2s;
}

.btn-copy:hover {
  background: #2980b9;
}

.btn-copy:active {
  transform: scale(0.95);
}

/* Tabs */
.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid #ecf0f1;
}

.tab {
  padding: 1rem 2rem;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  color: #7f8c8d;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: -2px;
}

.tab:hover {
  color: #3498db;
}

.tab.active {
  color: #3498db;
  border-bottom-color: #3498db;
}

/* Модальное окно */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.modal-ad-title {
  color: #7f8c8d;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 500;
}

.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.3s;
  resize: vertical;
}

.form-group textarea:focus {
  outline: none;
  border-color: #3498db;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
}
</style>