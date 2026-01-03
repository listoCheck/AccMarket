<template>
  <div class="container">
    <div class="form-container">
      <div class="card">
        <h1>Редактировать объявление</h1>
        
        <div v-if="loading" class="loading">Загрузка...</div>
        
        <form v-else @submit.prevent="handleSubmit">
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
            <label for="type">Тип объявления</label>
            <select id="type" v-model="form.type" required>
              <option value="SALE">Продажа</option>
              <option value="RENT">Аренда</option>
              <option value="FAMILY_SHARE">Семейный доступ</option>
            </select>
          </div>

          <div class="form-group">
            <label for="price">Цена (₽)</label>
            <input
              id="price"
              v-model.number="form.price"
              type="number"
              min="0"
              step="0.01"
              required
              placeholder="Введите цену"
            />
          </div>

          <div class="form-group">
            <label for="description">Описание</label>
            <textarea
              id="description"
              v-model="form.description"
              required
              placeholder="Подробно опишите ваше предложение"
              rows="6"
            ></textarea>
          </div>

          <div v-if="error" class="error">{{ error }}</div>
          <div v-if="success" class="success">{{ success }}</div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? 'Сохранение...' : 'Сохранить изменения' }}
            </button>
            <router-link to="/advertisements" class="btn btn-secondary">
              Отмена
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAdvertisementsStore } from '@/stores/advertisements'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const adsStore = useAdvertisementsStore()

const advertisementId = route.params.id as string

const form = ref({
  title: '',
  type: 'SALE',
  price: 0,
  description: ''
})

const loading = ref(true)
const submitting = ref(false)
const error = ref('')
const success = ref('')

async function loadAdvertisement() {
  loading.value = true
  error.value = ''

  try {
    // Загружаем все объявления и находим нужное
    await adsStore.fetchAdvertisements()
    const ad = adsStore.advertisements.find(a => a.id === advertisementId)
    
    if (ad) {
      // Проверяем, что пользователь - владелец объявления
      if (ad.userId !== authStore.userId) {
        error.value = 'У вас нет прав на редактирование этого объявления'
        setTimeout(() => router.push('/advertisements'), 2000)
        return
      }
      
      form.value = {
        title: ad.title,
        type: ad.type,
        price: ad.price,
        description: ad.description
      }
    } else {
      error.value = 'Объявление не найдено'
      setTimeout(() => router.push('/advertisements'), 2000)
    }
  } catch (err: any) {
    error.value = err.message || 'Ошибка загрузки объявления'
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  error.value = ''
  success.value = ''

  if (!authStore.username || !authStore.accessToken) {
    error.value = 'Необходимо войти в систему'
    submitting.value = false
    return
  }

  try {
    const result = await adsStore.editAdvertisement({
      username: authStore.username,
      token: authStore.accessToken,
      advertisementId,
      title: form.value.title,
      type: form.value.type,
      price: form.value.price,
      description: form.value.description
    })
    
    if (result.success) {
      success.value = 'Объявление успешно обновлено! Перенаправление...'
      setTimeout(() => {
        router.push('/advertisements')
      }, 1500)
    } else {
      error.value = result.message || 'Ошибка обновления объявления'
    }
  } catch (err: any) {
    error.value = err.message || 'Произошла ошибка'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadAdvertisement()
})
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 2rem;
  color: var(--primary-color);
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.form-actions .btn {
  flex: 1;
}
</style>