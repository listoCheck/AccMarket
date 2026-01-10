<template>
  <div class="appeals">
    <h1>Апелляции</h1>

    <div v-if="authStore.isAdmin" class="admin-section">
      <h2>Ожидающие рассмотрения</h2>

      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-if="success" class="alert alert-success">
        {{ success }}
      </div>

      <div v-if="loading" class="loading">Загрузка...</div>

      <div v-else-if="pendingAppeals.length === 0" class="empty-state">
        <p>Нет апелляций на рассмотрении</p>
      </div>

      <div v-else class="appeals-list">
        <div
          v-for="appeal in pendingAppeals"
          :key="appeal.id"
          class="appeal-card"
        >
          <div class="appeal-content">
            <h3>Апелляция #{{ appeal.id }}</h3>
            <p><strong>Объявление:</strong> {{ appeal.advertisementId }}</p>
            <p><strong>Причина:</strong> {{ appeal.reason }}</p>
            <p class="appeal-date">{{ formatDate(appeal.createdAt) }}</p>
          </div>

          <div class="appeal-actions">
            <button
              @click="decideAppeal(appeal, 'APPROVED')"
              class="btn btn-success btn-sm"
            >
              Одобрить
            </button>
            <button
              @click="decideAppeal(appeal, 'REJECTED')"
              class="btn btn-danger btn-sm"
            >
              Отклонить
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="user-section">
      <h2>Создать апелляцию</h2>

      <div v-if="createError" class="alert alert-error">
        {{ createError }}
      </div>

      <div v-if="createSuccess" class="alert alert-success">
        {{ createSuccess }}
      </div>

      <form @submit.prevent="createAppeal" class="appeal-form">
        <div class="form-group">
          <label for="advertisementId">ID объявления</label>
          <input
            id="advertisementId"
            v-model="appealForm.advertisementId"
            type="text"
            required
            placeholder="Введите ID отклоненного объявления"
          />
        </div>

        <div class="form-group">
          <label for="reason">Причина апелляции</label>
          <textarea
            id="reason"
            v-model="appealForm.reason"
            required
            rows="4"
            placeholder="Объясните, почему объявление должно быть одобрено"
          ></textarea>
        </div>

        <button type="submit" class="btn btn-primary" :disabled="createLoading">
          {{ createLoading ? 'Отправка...' : 'Отправить апелляцию' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { appealsAPI } from '../api/admin'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Appeals',
  setup() {
    const authStore = useAuthStore()
    const pendingAppeals = ref([])
    const loading = ref(false)
    const error = ref('')
    const success = ref('')

    const appealForm = ref({
      advertisementId: '',
      reason: ''
    })
    const createLoading = ref(false)
    const createError = ref('')
    const createSuccess = ref('')

    const fetchPendingAppeals = async () => {
      if (!authStore.isAdmin) return

      loading.value = true
      error.value = ''

      try {
        const response = await appealsAPI.getPending()
        pendingAppeals.value = response.data || []
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка загрузки апелляций'
      } finally {
        loading.value = false
      }
    }

    const decideAppeal = async (appeal, decision) => {
      error.value = ''
      success.value = ''

      try {
        const response = await appealsAPI.decide({
          username: authStore.username,
          token: authStore.accessToken,
          appealId: appeal.id,
          decision
        })

        if (response.data.success) {
          success.value = `Апелляция ${decision === 'APPROVED' ? 'одобрена' : 'отклонена'}`
          await fetchPendingAppeals()
        } else {
          error.value = response.data.message || 'Ошибка обработки апелляции'
        }
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка обработки апелляции'
      }
    }

    const createAppeal = async () => {
      createLoading.value = true
      createError.value = ''
      createSuccess.value = ''

      try {
        const response = await appealsAPI.create({
          username: authStore.username,
          token: authStore.accessToken,
          advertisementId: appealForm.value.advertisementId,
          reason: appealForm.value.reason
        })

        if (response.data.success) {
          createSuccess.value = 'Апелляция успешно создана'
          appealForm.value = {
            advertisementId: '',
            reason: ''
          }
        } else {
          createError.value = response.data.message || 'Ошибка создания апелляции'
        }
      } catch (err) {
        createError.value = err.response?.data?.message || 'Ошибка создания апелляции'
      } finally {
        createLoading.value = false
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ru-RU')
    }

    onMounted(() => {
      fetchPendingAppeals()
    })

    return {
      authStore,
      pendingAppeals,
      loading,
      error,
      success,
      appealForm,
      createLoading,
      createError,
      createSuccess,
      decideAppeal,
      createAppeal,
      formatDate
    }
  }
}
</script>

<style scoped>
.appeals {
  padding: 2rem 0;
}

.appeals h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.admin-section,
.user-section {
  margin-bottom: 3rem;
}

.admin-section h2,
.user-section h2 {
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
  color: #7f8c8d;
}

.appeals-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.appeal-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.appeal-content {
  flex: 1;
}

.appeal-content h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.appeal-content p {
  color: #7f8c8d;
  margin-bottom: 0.5rem;
}

.appeal-date {
  font-size: 0.875rem;
  color: #95a5a6;
}

.appeal-actions {
  display: flex;
  gap: 0.5rem;
  flex-direction: column;
}

.appeal-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  max-width: 600px;
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

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3498db;
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