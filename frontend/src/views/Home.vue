<template>
  <div class="home">
    <section class="hero">
      <h1>Добро пожаловать в AccMarket</h1>
      <p>Маркетплейс для покупки и продажи игровых аккаунтов</p>
      <div class="hero-actions">
        <router-link to="/advertisements" class="btn btn-primary btn-large">
          Посмотреть объявления
        </router-link>
        <router-link v-if="!authStore.isAuthenticated" to="/register" class="btn btn-secondary btn-large">
          Зарегистрироваться
        </router-link>
      </div>
    </section>

    <section class="features">
      <h2>Преимущества платформы</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🔒</div>
          <h3>Безопасность</h3>
          <p>Защита данных и безопасные транзакции</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">⚡</div>
          <h3>Быстрота</h3>
          <p>Мгновенное размещение объявлений</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">✅</div>
          <h3>Модерация</h3>
          <p>Проверка всех объявлений модераторами</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">💰</div>
          <h3>Удобство</h3>
          <p>Встроенная система баланса</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Home',
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  }
}
</script>

<style scoped>
.home {
  padding: 2rem 0;
}

.hero {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.features {
  padding: 2rem 0;
}

.features h2 {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #2c3e50;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  text-align: center;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.feature-card p {
  color: #7f8c8d;
}

.btn-large {
  padding: 1rem 2rem;
  font-size: 1.1rem;
}
</style>