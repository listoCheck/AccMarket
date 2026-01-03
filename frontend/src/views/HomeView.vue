<template>
  <div class="container">
    <div class="hero">
      <h1>AccMarket</h1>
      <p class="subtitle">Биржа игровых аккаунтов Steam и других сервисов</p>
      
      <div class="hero-actions">
        <router-link to="/advertisements" class="btn btn-primary">
          Посмотреть объявления
        </router-link>
        <router-link v-if="!authStore.isAuthenticated" to="/register" class="btn btn-secondary">
          Начать продавать
        </router-link>
        <router-link v-else to="/advertisements/create" class="btn btn-secondary">
          Создать объявление
        </router-link>
      </div>
    </div>

    <div class="features">
      <div class="feature-card card">
        <h3>🎮 Аренда аккаунтов</h3>
        <p>Получите временный доступ к играм без необходимости покупки</p>
      </div>

      <div class="feature-card card">
        <h3>💰 Продажа аккаунтов</h3>
        <p>Монетизируйте свои игровые аккаунты безопасно</p>
      </div>

      <div class="feature-card card">
        <h3>👨‍👩‍👧‍👦 Семейный доступ</h3>
        <p>Делитесь библиотекой игр с другими пользователями</p>
      </div>

      <div class="feature-card card">
        <h3>🔒 Безопасность</h3>
        <p>Все сделки проходят модерацию для защиты от мошенничества</p>
      </div>
    </div>

    <div class="stats">
      <div class="stat-item">
        <h2>{{ stats.users }}+</h2>
        <p>Пользователей</p>
      </div>
      <div class="stat-item">
        <h2>{{ stats.ads }}+</h2>
        <p>Объявлений</p>
      </div>
      <div class="stat-item">
        <h2>{{ stats.deals }}+</h2>
        <p>Успешных сделок</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const stats = ref({
  users: 1000,
  ads: 500,
  deals: 2500
})
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 4rem 0;
}

.hero h1 {
  font-size: 3rem;
  color: var(--primary-color);
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.25rem;
  color: #666;
  margin-bottom: 2rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin: 4rem 0;
}

.feature-card {
  text-align: center;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--primary-color);
}

.stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 2rem;
  padding: 3rem 0;
  background: var(--card-bg);
  border-radius: 8px;
  margin: 2rem 0;
}

.stat-item {
  text-align: center;
}

.stat-item h2 {
  font-size: 2.5rem;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.stat-item p {
  color: #666;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 2rem;
  }
  
  .subtitle {
    font-size: 1rem;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}
</style>