<template>
  <div class="users-management">
    <h1>Управление пользователями</h1>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="success" class="alert alert-success">
      {{ success }}
    </div>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else class="users-list">
      <div v-for="user in users" :key="user.id" class="user-card">
        <div class="user-info">
          <h3>{{ user.username }}</h3>
          <p class="user-email">{{ user.email }}</p>
          <div class="user-roles">
            <span
              v-for="role in user.roles"
              :key="role"
              class="role-badge"
            >
              {{ role }}
            </span>
          </div>
        </div>

        <div class="user-actions">
          <button
            v-if="!user.roles.includes('ADMIN')"
            @click="assignRole(user, 'ADMIN')"
            class="btn btn-primary btn-sm"
          >
            Назначить админом
          </button>
          <button
            v-if="!user.roles.includes('MODERATOR') && !user.roles.includes('ADMIN')"
            @click="assignRole(user, 'MODERATOR')"
            class="btn btn-secondary btn-sm"
          >
            Назначить модератором
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { adminAPI } from '../../api/admin'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'Users',
  setup() {
    const authStore = useAuthStore()
    const users = ref([])
    const loading = ref(false)
    const error = ref('')
    const success = ref('')

    const fetchUsers = async () => {
      loading.value = true
      error.value = ''

      try {
        const response = await adminAPI.getAllUsers({
          username: authStore.username,
          token: authStore.accessToken
        })

        if (response.data.success) {
          users.value = response.data.data || []
        } else {
          error.value = response.data.message || 'Ошибка загрузки пользователей'
        }
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка загрузки пользователей'
      } finally {
        loading.value = false
      }
    }

    const assignRole = async (user, role) => {
      error.value = ''
      success.value = ''

      try {
        let response
        if (role === 'ADMIN') {
          response = await adminAPI.assignAdmin({
            username: authStore.username,
            token: authStore.accessToken,
            targetUsername: user.username,
            secretKey: prompt('Введите секретный ключ для назначения администратора:')
          })
        } else if (role === 'MODERATOR') {
          response = await adminAPI.assignModerator({
            username: authStore.username,
            token: authStore.accessToken,
            targetUsername: user.username
          })
        }

        if (response.data.success) {
          success.value = `Роль ${role} успешно назначена пользователю ${user.username}`
          await fetchUsers()
        } else {
          error.value = response.data.message || 'Ошибка назначения роли'
        }
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка назначения роли'
      }
    }

    onMounted(() => {
      fetchUsers()
    })

    return {
      users,
      loading,
      error,
      success,
      assignRole
    }
  }
}
</script>

<style scoped>
.users-management {
  padding: 2rem 0;
}

.users-management h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #7f8c8d;
}

.users-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.user-info {
  flex: 1;
}

.user-info h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.user-email {
  color: #7f8c8d;
  margin-bottom: 0.5rem;
}

.user-roles {
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

.user-actions {
  display: flex;
  gap: 0.5rem;
  flex-direction: column;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  white-space: nowrap;
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