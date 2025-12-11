<template>
  <div class="admin-panel">
    <h2>Панель администратора</h2>
    
    <div class="admin-notice">
      <p>⚠️ Функционал администрирования находится в разработке</p>
      <p>Скоро здесь будут доступны:</p>
      <ul>
        <li>Управление пользователями</li>
        <li>Назначение ролей (модераторы, администраторы)</li>
        <li>Просмотр всех объявлений</li>
        <li>Модерация контента</li>
      </ul>
    </div>

    <div class="placeholder-section">
      <h3>Пользователи</h3>
      <div class="placeholder-content">
        <p>Список пользователей будет отображаться здесь</p>
      </div>
    </div>

    <div class="placeholder-section">
      <h3>Статистика</h3>
      <div class="placeholder-content">
        <p>Статистика платформы будет отображаться здесь</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminPanel'
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

.admin-notice {
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.admin-notice p {
  margin: 10px 0;
  color: #856404;
}

.admin-notice ul {
  margin: 15px 0;
  padding-left: 20px;
  color: #856404;
}

.admin-notice li {
  margin: 8px 0;
}

.placeholder-section {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.placeholder-section h3 {
  color: #333;
  margin-bottom: 15px;
}

.placeholder-content {
  padding: 40px;
  text-align: center;
  color: #999;
  background: #f5f5f5;
  border-radius: 4px;
}
</style>