<template>
  <footer class="footer">
    <div class="footer-container">
      <p>&copy; 2024 AccMarket. Все права защищены.</p>
      <div class="footer-links">
        <a href="#" class="footer-link">О проекте</a>
        <a href="#" class="footer-link">Правила</a>
        <a href="#" class="footer-link">Контакты</a>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style scoped>
.footer {
  background: #34495e;
  color: white;
  padding: 2rem 0;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.footer-links {
  margin-top: 1rem;
  display: flex;
  gap: 2rem;
  justify-content: center;
}

.footer-link {
  color: #ecf0f1;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #3498db;
}
</style>