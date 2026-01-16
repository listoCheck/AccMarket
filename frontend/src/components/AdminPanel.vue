<template>
  <div class="admin-panel">
    <h2>Панель администратора</h2>
    
    <div class="admin-tabs">
      <button
        :class="['tab-btn', { active: activeTab === 'users' }]"
        @click="activeTab = 'users'"
      >
        Пользователи
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'ads' }]"
        @click="activeTab = 'ads'"
      >
        Объявления
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'roles' }]"
        @click="activeTab = 'roles'"
      >
        Управление ролями
      </button>
    </div>

    <!-- Users Tab -->
    <div v-if="activeTab === 'users'" class="tab-content">
      <div class="section-header">
        <h3>Все пользователи</h3>
        <button @click="loadUsers" class="btn btn-primary btn-small">
          Обновить
        </button>
      </div>

      <div v-if="loadingUsers" class="loading">Загрузка пользователей...</div>
      <div v-else-if="usersError" class="error-message">{{ usersError }}</div>
      <div v-else-if="users.length === 0" class="no-data">Пользователи не найдены</div>
      
      <div v-else class="users-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Имя пользователя</th>
              <th>Email</th>
              <th>Роли</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span v-for="role in user.roles" :key="role" class="role-badge">
                  {{ role }}
                </span>
              </td>
              <td>
                <button
                  @click="openRoleModal(user)"
                  class="btn btn-small btn-secondary"
                >
                  Изменить роли
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Advertisements Tab -->
    <div v-if="activeTab === 'ads'" class="tab-content">
      <div class="section-header">
        <h3>Все объявления</h3>
        <div class="filters">
          <input
            v-model="adUserId"
            type="text"
            placeholder="ID пользователя"
            class="filter-input"
          />
          <button @click="loadAdvertisements" class="btn btn-primary btn-small">
            Загрузить
          </button>
        </div>
      </div>

      <div v-if="loadingAds" class="loading">Загрузка объявлений...</div>
      <div v-else-if="adsError" class="error-message">{{ adsError }}</div>
      <div v-else-if="advertisements.length === 0" class="no-data">
        Объявления не найдены
      </div>
      
      <div v-else class="ads-grid">
        <div v-for="ad in advertisements" :key="ad.id" class="ad-card">
          <div class="ad-header">
            <h4>{{ ad.title }}</h4>
            <span class="ad-price">{{ ad.cost }} ₽</span>
          </div>
          <p class="ad-description">{{ ad.text }}</p>
          <div class="ad-meta">
            <span class="ad-info">ID: {{ ad.id }}</span>
            <span class="ad-info">User ID: {{ ad.userId }}</span>
          </div>
          <div v-if="ad.platform || ad.genre" class="ad-tags">
            <span v-if="ad.platform" class="tag">{{ ad.platform }}</span>
            <span v-if="ad.genre" class="tag">{{ ad.genre }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Roles Management Tab -->
    <div v-if="activeTab === 'roles'" class="tab-content">
      <div class="roles-section">
        <h3>Назначить администратора</h3>
        <form @submit.prevent="assignAdmin" class="role-form">
          <div class="form-row">
            <div class="form-group">
              <label>Имя пользователя</label>
              <input v-model="adminForm.username" type="text" required />
            </div>
            <div class="form-group">
              <label>Email</label>
              <input v-model="adminForm.email" type="email" required />
            </div>
          </div>
          <div class="form-group">
            <label>Секретный ключ администратора</label>
            <input v-model="adminForm.adminSecret" type="password" required />
          </div>
          <button type="submit" class="btn btn-primary" :disabled="loadingAction">
            {{ loadingAction ? 'Назначение...' : 'Назначить администратора' }}
          </button>
        </form>
      </div>

      <div class="roles-section">
        <h3>Назначить модератора</h3>
        <form @submit.prevent="assignModerator" class="role-form">
          <div class="form-group">
            <label>ID модератора (UUID)</label>
            <input v-model="moderatorForm.moderatorId" type="text" required />
          </div>
          <button type="submit" class="btn btn-primary" :disabled="loadingAction">
            {{ loadingAction ? 'Назначение...' : 'Назначить модератора' }}
          </button>
        </form>
      </div>

      <div v-if="actionMessage" :class="['action-message', actionMessageType]">
        {{ actionMessage }}
      </div>
    </div>

    <!-- Role Edit Modal -->
    <div v-if="showRoleModal" class="modal-overlay" @click.self="closeRoleModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Изменить роли: {{ selectedUser?.username }}</h3>
          <button @click="closeRoleModal" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="updateUserRoles">
          <div class="form-group">
            <label>Роли (через запятую)</label>
            <input
              v-model="roleInput"
              type="text"
              placeholder="ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR"
            />
            <small>Доступные роли: ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR</small>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeRoleModal" class="btn btn-secondary">
              Отмена
            </button>
            <button type="submit" class="btn btn-primary" :disabled="loadingAction">
              {{ loadingAction ? 'Сохранение...' : 'Сохранить' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

export default {
  name: 'AdminPanel',
  setup() {
    const authStore = useAuthStore()
    
    const activeTab = ref('users')
    const users = ref([])
    const advertisements = ref([])
    const loadingUsers = ref(false)
    const loadingAds = ref(false)
    const loadingAction = ref(false)
    const usersError = ref('')
    const adsError = ref('')
    const adUserId = ref('')
    
    const showRoleModal = ref(false)
    const selectedUser = ref(null)
    const roleInput = ref('')
    
    const adminForm = ref({
      username: '',
      email: '',
      adminSecret: ''
    })
    
    const moderatorForm = ref({
      moderatorId: ''
    })
    
    const actionMessage = ref('')
    const actionMessageType = ref('success')

    const loadUsers = async () => {
      loadingUsers.value = true
      usersError.value = ''
      
      try {
        const response = await api.admin.getAllUsers(
          authStore.username,
          authStore.accessToken
        )
        
        if (response.data.code === 200 && response.data.body) {
          users.value = response.data.body
        } else {
          usersError.value = response.data.message || 'Ошибка загрузки пользователей'
        }
      } catch (error) {
        usersError.value = error.response?.data?.message || 'Ошибка загрузки пользователей'
      } finally {
        loadingUsers.value = false
      }
    }

    const loadAdvertisements = async () => {
      loadingAds.value = true
      adsError.value = ''
      
      try {
        const params = {}
        if (adUserId.value) {
          params.userId = adUserId.value
        }
        
        const response = await api.admin.getAdvertisements(params)
        
        if (response.data) {
          advertisements.value = Array.isArray(response.data) 
            ? response.data 
            : [response.data]
        }
      } catch (error) {
        adsError.value = error.response?.data?.message || 'Ошибка загрузки объявлений'
      } finally {
        loadingAds.value = false
      }
    }

    const openRoleModal = (user) => {
      selectedUser.value = user
      roleInput.value = user.roles ? user.roles.join(', ') : ''
      showRoleModal.value = true
    }

    const closeRoleModal = () => {
      showRoleModal.value = false
      selectedUser.value = null
      roleInput.value = ''
    }

    const updateUserRoles = async () => {
      if (!selectedUser.value) return
      
      loadingAction.value = true
      
      try {
        const roles = roleInput.value
          .split(',')
          .map(r => r.trim())
          .filter(r => r)
        
        await api.admin.updateUserRoles({
          username: authStore.username,
          token: authStore.accessToken,
          targetUsername: selectedUser.value.username,
          roles
        })
        
        closeRoleModal()
        loadUsers()
        showActionMessage('Роли успешно обновлены', 'success')
      } catch (error) {
        showActionMessage(
          error.response?.data?.message || 'Ошибка обновления ролей',
          'error'
        )
      } finally {
        loadingAction.value = false
      }
    }

    const assignAdmin = async () => {
      loadingAction.value = true
      
      try {
        await api.admin.assignAdmin({
          username: adminForm.value.username,
          token: authStore.accessToken,
          adminSecret: adminForm.value.adminSecret,
          email: adminForm.value.email
        })
        
        adminForm.value = { username: '', email: '', adminSecret: '' }
        showActionMessage('Администратор успешно назначен', 'success')
        loadUsers()
      } catch (error) {
        showActionMessage(
          error.response?.data?.message || 'Ошибка назначения администратора',
          'error'
        )
      } finally {
        loadingAction.value = false
      }
    }

    const assignModerator = async () => {
      loadingAction.value = true
      
      try {
        await api.admin.assignModerator({
          adminId: authStore.username,
          adminToken: authStore.accessToken,
          moderatorId: moderatorForm.value.moderatorId
        })
        
        moderatorForm.value = { moderatorId: '' }
        showActionMessage('Модератор успешно назначен', 'success')
        loadUsers()
      } catch (error) {
        showActionMessage(
          error.response?.data?.message || 'Ошибка назначения модератора',
          'error'
        )
      } finally {
        loadingAction.value = false
      }
    }

    const showActionMessage = (message, type) => {
      actionMessage.value = message
      actionMessageType.value = type
      setTimeout(() => {
        actionMessage.value = ''
      }, 5000)
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      activeTab,
      users,
      advertisements,
      loadingUsers,
      loadingAds,
      loadingAction,
      usersError,
      adsError,
      adUserId,
      showRoleModal,
      selectedUser,
      roleInput,
      adminForm,
      moderatorForm,
      actionMessage,
      actionMessageType,
      loadUsers,
      loadAdvertisements,
      openRoleModal,
      closeRoleModal,
      updateUserRoles,
      assignAdmin,
      assignModerator
    }
  }
}
</script>

<style scoped>
.admin-panel {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.admin-panel h2 {
  color: #333;
  margin-bottom: 30px;
}

.admin-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid #e0e0e0;
}

.tab-btn {
  padding: 12px 24px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.3s;
}

.tab-btn:hover {
  color: #4CAF50;
}

.tab-btn.active {
  color: #4CAF50;
  border-bottom-color: #4CAF50;
}

.tab-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #333;
}

.filters {
  display: flex;
  gap: 10px;
}

.filter-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
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

.users-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

th {
  background: #f5f5f5;
  font-weight: 600;
  color: #333;
}

.role-badge {
  display: inline-block;
  padding: 4px 8px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 5px;
}

.ads-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.ad-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
}

.ad-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 10px;
}

.ad-header h4 {
  margin: 0;
  color: #333;
  font-size: 16px;
}

.ad-price {
  font-weight: bold;
  color: #4CAF50;
  white-space: nowrap;
}

.ad-description {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.ad-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}

.ad-info {
  font-size: 12px;
  color: #999;
}

.ad-tags {
  display: flex;
  gap: 5px;
}

.tag {
  display: inline-block;
  padding: 4px 8px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.roles-section {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.roles-section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.role-form {
  max-width: 600px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group small {
  display: block;
  margin-top: 5px;
  color: #999;
  font-size: 12px;
}

.action-message {
  padding: 15px;
  border-radius: 4px;
  margin-top: 20px;
}

.action-message.success {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #4caf50;
}

.action-message.error {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #f44336;
}

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
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  padding: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 32px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  line-height: 1;
}

.close-btn:hover {
  color: #333;
}

.modal-content form {
  padding: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #45a049;
}

.btn-secondary {
  background-color: #2196F3;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #0b7dda;
}

.btn-small {
  padding: 6px 12px;
  font-size: 13px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>