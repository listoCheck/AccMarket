<template>
  <div class="edit-advertisement">
    <h1>Редактировать объявление</h1>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="success" class="alert alert-success">
      {{ success }}
    </div>

    <form v-if="form.title" @submit.prevent="handleSubmit" class="ad-form">
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
        <label for="description">Описание</label>
        <textarea
          id="description"
          v-model="form.description"
          required
          rows="6"
          placeholder="Подробное описание товара/услуги"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="price">Цена (₽)</label>
        <input
          id="price"
          v-model.number="form.price"
          type="number"
          required
          min="0"
          step="0.01"
          placeholder="0.00"
        />
      </div>

      <div class="form-group">
        <label for="type">Тип</label>
        <input
          id="type"
          v-model="form.type"
          type="text"
          required
          placeholder="Например: Steam, Epic Games, Origin"
        />
      </div>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Сохранение...' : 'Сохранить изменения' }}
        </button>
        <router-link to="/advertisements" class="btn btn-secondary">
          Отмена
        </router-link>
      </div>
    </form>

    <div v-else class="loading">
      Загрузка данных объявления...
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdvertisementsStore } from '../stores/advertisements'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'EditAdvertisement',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const adsStore = useAdvertisementsStore()
    const authStore = useAuthStore()

    const form = ref({
      id: '',
      title: '',
      description: '',
      price: 0,
      type: ''
    })

    const loading = ref(false)
    const error = ref('')
    const success = ref('')

    const loadAdvertisement = () => {
      const currentAd = adsStore.currentAdvertisement
      if (currentAd) {
        form.value = {
          id: currentAd.id,
          title: currentAd.title,
          description: currentAd.description,
          price: currentAd.price,
          type: currentAd.type
        }
      } else {
        error.value = 'Объявление не найдено'
        setTimeout(() => {
          router.push('/advertisements')
        }, 2000)
      }
    }

    const handleSubmit = async () => {
      loading.value = true
      error.value = ''
      success.value = ''

      try {
        const result = await adsStore.updateAdvertisement({
          username: authStore.username,
          token: authStore.accessToken,
          advertisementId: form.value.id,
          title: form.value.title,
          description: form.value.description,
          price: form.value.price,
          type: form.value.type
        })

        if (result.success) {
          success.value = 'Объявление успешно обновлено! Перенаправление...'
          setTimeout(() => {
            router.push('/advertisements')
          }, 1500)
        } else {
          error.value = result.message || 'Ошибка обновления объявления'
        }
      } catch (err) {
        error.value = 'Произошла ошибка при обновлении объявления'
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadAdvertisement()
    })

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
.edit-advertisement {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem 0;
}

.edit-advertisement h1 {
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

.loading {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
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