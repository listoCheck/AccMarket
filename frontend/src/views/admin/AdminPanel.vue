<template>
  <div class="admin-panel">
    <h1>Админ-панель</h1>

    <div class="admin-cards">
      <router-link to="/admin/users" class="admin-card">
        <div class="card-icon">👥</div>
        <h3>Управление пользователями</h3>
        <p>Назначение ролей и управление правами</p>
      </router-link>

      <router-link to="/admin/moderation" class="admin-card">
        <div class="card-icon">✅</div>
        <h3>Модерация</h3>
        <p>Проверка и одобрение объявлений</p>
      </router-link>

      <router-link to="/appeals" class="admin-card">
        <div class="card-icon">📝</div>
        <h3>Апелляции</h3>
        <p>Рассмотрение жалоб пользователей</p>
      </router-link>
    </div>

    <div class="stats">
      <h2>Статистика</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <h3>Всего пользователей</h3>
          <p class="stat-value">{{ stats.totalUsers }}</p>
        </div>
        <div class="stat-card">
          <h3>Активных объявлений</h3>
          <p class="stat-value">{{ stats.activeAds }}</p>
        </div>
        <div class="stat-card">
          <h3>На модерации</h3>
          <p class="stat-value">{{ stats.pendingAds }}</p>
        </div>
        <div class="stat-card">
          <h3>Апелляций</h3>
          <p class="stat-value">{{ stats.pendingAppeals }}</p>
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
  name: 'AdminPanel',
  setup() {
    const authStore = useAuthStore()
    const stats = ref({
      totalUsers: 0,
      activeAds: 0,
      pendingAds: 0,
      pendingAppeals: 0
    })

    const fetchStats = async () => {
      try {
        // Fetch users count
        const usersResponse = await adminAPI.getAllUsers({
          username: authStore.username,
          token: authStore.accessToken
        })
        if (usersResponse.data.success) {
          stats.value.totalUsers = usersResponse.data.data?.length || 0
        }
      } catch (error) {
        console.error('Error fetching stats:', error)
      }
    }

    onMounted(() => {
      fetchStats()
    })

    return {
      stats
    }
  }
}
</script>

<style scoped>
.admin-panel {
  padding: 2rem 0;
}

.admin-panel h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.admin-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.admin-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s, box-shadow 0.3s;
  text-align: center;
}

.admin-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.admin-card h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.admin-card p {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.stats {
  margin-top: 3rem;
}

.stats h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-card h3 {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
  font-weight: normal;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #3498db;
  margin: 0;
}
</style>