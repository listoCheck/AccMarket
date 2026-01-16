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
            <p><strong>Объявление:</strong> {{ appeal.adTitle }}</p>
            <p><strong>Причина апелляции:</strong> {{ appeal.reason }}</p>
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

    <div v-else class="empty-state">
      <p>У вас нет доступа к этой странице</p>
      <router-link to="/profile" class="btn btn-primary">
        Перейти в профиль
      </router-link>
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

      const comment = decision === 'REJECTED'
        ? prompt('Укажите причину отклонения апелляции:')
        : null

      if (decision === 'REJECTED' && !comment) {
        return
      }

      try {
        const response = await appealsAPI.decide({
          appealId: appeal.id,
          status: decision,
          decision: comment
        })

        if (response.status === 200 && response.data) {
          success.value = `Апелляция ${decision === 'APPROVED' ? 'одобрена' : 'отклонена'}`
          await fetchPendingAppeals()
        } else {
          error.value = 'Ошибка обработки апелляции'
        }
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка обработки апелляции'
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
      decideAppeal,
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

.admin-section {
  margin-bottom: 3rem;
}

.admin-section h2 {
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

.empty-state p {
  margin-bottom: 1rem;
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