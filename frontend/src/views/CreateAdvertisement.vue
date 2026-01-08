<template>
  <div class="create-advertisement">
    <h1>Создать объявление</h1>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="success" class="alert alert-success">
      {{ success }}
    </div>

    <form @submit.prevent="handleSubmit" class="ad-form">
      <div class="form-group">
        <label for="title">Название</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          required
          placeholder="Введите название объявления"
        />
      </div>

      <div class="form-group">
        <label for="text">Описание</label>
        <textarea
          id="text"
          v-model="form.text"
          required
          rows="6"
          placeholder="Подробное описание товара/услуги"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="cost">Цена (₽)</label>
        <input
          id="cost"
          v-model.number="form.cost"
          type="number"
          required
          min="0"
          step="1"
          placeholder="0"
        />
      </div>

      <div class="form-group">
        <label for="platform">Платформа</label>
        <input
          id="platform"
          v-model="form.platform"
          type="text"
          required
          placeholder="Например: Steam, Epic Games, Origin"
        />
      </div>

      <div class="form-group">
        <label for="genre">Жанр</label>
        <input
          id="genre"
          v-model="form.genre"
          type="text"
          required
          placeholder="Например: Action, RPG, Strategy"
        />
      </div>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Создание...' : 'Создать объявление' }}
        </button>
        <router-link to="/advertisements" class="btn btn-secondary">
          Отмена
        </router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAdvertisementsStore } from '../stores/advertisements'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'CreateAdvertisement',
  setup() {
    const router = useRouter()
    const adsStore = useAdvertisementsStore()
    const authStore = useAuthStore()

    const form = ref({
      title: '',
      text: '',
      cost: 0,
      platform: '',
      genre: ''
    })

    const loading = ref(false)
    const error = ref('')
    const success = ref('')

    const handleSubmit = async () => {
      loading.value = true
      error.value = ''
      success.value = ''

      try {
        const result = await adsStore.createAdvertisement({
          username: authStore.username,
          token: authStore.accessToken,
          title: form.value.title,
          text: form.value.text,
          cost: form.value.cost,
          platform: form.value.platform,
          genre: form.value.genre,
          advertisementId: null
        })

        if (result.success) {
          success.value = 'Объявление успешно создано! Перенаправление...'
          setTimeout(() => {
            router.push('/advertisements')
          }, 1500)
        } else {
          error.value = result.message || 'Ошибка создания объявления'
        }
      } catch (err) {
        error.value = 'Произошла ошибка при создании объявления'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      error,
      success,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.create-advertisement {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem 0;
}

.create-advertisement h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.ad-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
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