<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>{{ isEdit ? 'Редактировать объявление' : 'Создать объявление' }}</h2>
        <button @click="$emit('close')" class="close-btn">&times;</button>
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="title">Название *</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            required
            placeholder="Введите название объявления"
          />
        </div>

        <div class="form-group">
          <label for="text">Описание *</label>
          <textarea
            id="text"
            v-model="form.text"
            required
            rows="5"
            placeholder="Подробное описание аккаунта"
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="cost">Цена (₽) *</label>
            <input
              id="cost"
              v-model.number="form.cost"
              type="number"
              required
              min="0"
              placeholder="0"
            />
          </div>

          <div class="form-group">
            <label for="platform">Платформа</label>
            <input
              id="platform"
              v-model="form.platform"
              type="text"
              placeholder="PC, PS5, Xbox и т.д."
            />
          </div>
        </div>

        <div class="form-group">
          <label for="genre">Жанр/Игра</label>
          <input
            id="genre"
            v-model="form.genre"
            type="text"
            placeholder="Например: MMORPG, Shooter"
          />
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <div class="form-actions">
          <button type="button" @click="$emit('close')" class="btn btn-secondary">
            Отмена
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Сохранение...' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'

export default {
  name: 'AdvertisementForm',
  props: {
    advertisement: {
      type: Object,
      default: null
    }
  },
  emits: ['close', 'saved'],
  setup(props, { emit }) {
    const authStore = useAuthStore()
    const loading = ref(false)
    const error = ref('')

    const form = ref({
      title: '',
      text: '',
      cost: 0,
      platform: '',
      genre: ''
    })

    const isEdit = computed(() => !!props.advertisement)

    onMounted(() => {
      if (props.advertisement) {
        form.value = {
          title: props.advertisement.title || '',
          text: props.advertisement.text || '',
          cost: props.advertisement.cost || 0,
          platform: props.advertisement.platform || '',
          genre: props.advertisement.genre || ''
        }
      }
    })

    const handleSubmit = async () => {
      error.value = ''
      loading.value = true

      try {
        const data = {
          username: authStore.username,
          token: authStore.accessToken,
          title: form.value.title,
          text: form.value.text,
          cost: form.value.cost,
          platform: form.value.platform || '',
          genre: form.value.genre || ''
        }

        if (isEdit.value) {
          data.advertisementId = props.advertisement.id
          await api.advertisements.update(data)
        } else {
          await api.advertisements.create(data)
        }

        emit('saved')
        emit('close')
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка сохранения объявления'
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      error,
      isEdit,
      handleSubmit
    }
  }
}
</script>

<style scoped>
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
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h2 {
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

form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4CAF50;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.error-message {
  color: #f44336;
  margin-bottom: 15px;
  padding: 10px;
  background: #ffebee;
  border-radius: 4px;
  font-size: 14px;
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
  background-color: #757575;
  color: white;
}

.btn-secondary:hover {
  background-color: #616161;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>