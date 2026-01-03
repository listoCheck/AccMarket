<template>
  <div class="container">
    <h1>Баланс</h1>

    <div class="balance-card card">
      <div v-if="loading" class="loading">Загрузка баланса...</div>
      
      <div v-else>
        <div class="balance-amount">
          <span class="label">Текущий баланс:</span>
          <span class="amount">{{ balance }} ₽</span>
        </div>

        <div class="balance-actions">
          <button @click="showDepositModal = true" class="btn btn-primary">
            Пополнить баланс
          </button>
          <button @click="showWithdrawModal = true" class="btn btn-secondary">
            Вывести средства
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно пополнения -->
    <div v-if="showDepositModal" class="modal">
      <div class="modal-content card">
        <h2>Пополнение баланса</h2>
        
        <div class="form-group">
          <label for="depositAmount">Сумма (₽)</label>
          <input
            id="depositAmount"
            v-model.number="depositAmount"
            type="number"
            min="1"
            step="0.01"
            placeholder="Введите сумму"
          />
        </div>

        <div v-if="error" class="error">{{ error }}</div>
        <div v-if="success" class="success">{{ success }}</div>

        <div class="modal-actions">
          <button @click="handleDeposit" class="btn btn-primary" :disabled="processing">
            {{ processing ? 'Обработка...' : 'Пополнить' }}
          </button>
          <button @click="closeModals" class="btn btn-secondary">
            Отмена
          </button>
        </div>
      </div>
    </div>

    <!-- Модальное окно вывода -->
    <div v-if="showWithdrawModal" class="modal">
      <div class="modal-content card">
        <h2>Вывод средств</h2>
        
        <div class="form-group">
          <label for="withdrawAmount">Сумма (₽)</label>
          <input
            id="withdrawAmount"
            v-model.number="withdrawAmount"
            type="number"
            min="1"
            :max="balance"
            step="0.01"
            placeholder="Введите сумму"
          />
        </div>

        <div v-if="error" class="error">{{ error }}</div>
        <div v-if="success" class="success">{{ success }}</div>

        <div class="modal-actions">
          <button @click="handleWithdraw" class="btn btn-primary" :disabled="processing">
            {{ processing ? 'Обработка...' : 'Вывести' }}
          </button>
          <button @click="closeModals" class="btn btn-secondary">
            Отмена
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/api/client'

const authStore = useAuthStore()

const balance = ref(0)
const loading = ref(true)
const processing = ref(false)
const error = ref('')
const success = ref('')

const showDepositModal = ref(false)
const showWithdrawModal = ref(false)
const depositAmount = ref(0)
const withdrawAmount = ref(0)

async function loadBalance() {
  if (!authStore.userId) return

  loading.value = true
  error.value = ''

  try {
    const response = await apiClient.getBalance(authStore.userId)
    if (response.data) {
      balance.value = response.data.money || 0
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Ошибка загрузки баланса'
  } finally {
    loading.value = false
  }
}

async function handleDeposit() {
  if (!authStore.userId || !authStore.accessToken) return
  
  if (depositAmount.value <= 0) {
    error.value = 'Введите корректную сумму'
    return
  }

  processing.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await apiClient.deposit({
      userId: authStore.userId,
      amount: depositAmount.value,
      token: authStore.accessToken
    })

    if (response.data.success) {
      success.value = 'Баланс успешно пополнен'
      await loadBalance()
      setTimeout(() => {
        closeModals()
      }, 1500)
    } else {
      error.value = response.data.message || 'Ошибка пополнения'
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Ошибка пополнения баланса'
  } finally {
    processing.value = false
  }
}

async function handleWithdraw() {
  if (!authStore.userId || !authStore.accessToken) return
  
  if (withdrawAmount.value <= 0) {
    error.value = 'Введите корректную сумму'
    return
  }

  if (withdrawAmount.value > balance.value) {
    error.value = 'Недостаточно средств'
    return
  }

  processing.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await apiClient.withdraw({
      userId: authStore.userId,
      amount: withdrawAmount.value,
      token: authStore.accessToken
    })

    if (response.data.success) {
      success.value = 'Средства успешно выведены'
      await loadBalance()
      setTimeout(() => {
        closeModals()
      }, 1500)
    } else {
      error.value = response.data.message || 'Ошибка вывода'
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Ошибка вывода средств'
  } finally {
    processing.value = false
  }
}

function closeModals() {
  showDepositModal.value = false
  showWithdrawModal.value = false
  depositAmount.value = 0
  withdrawAmount.value = 0
  error.value = ''
  success.value = ''
}

onMounted(() => {
  loadBalance()
})
</script>

<style scoped>
h1 {
  margin-bottom: 2rem;
}

.balance-card {
  max-width: 600px;
  margin: 0 auto;
}

.balance-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border-radius: 8px;
  color: white;
  margin-bottom: 2rem;
}

.balance-amount .label {
  font-size: 1.25rem;
}

.balance-amount .amount {
  font-size: 2.5rem;
  font-weight: bold;
}

.balance-actions {
  display: flex;
  gap: 1rem;
}

.balance-actions .btn {
  flex: 1;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  max-width: 400px;
  width: 90%;
}

.modal-content h2 {
  margin-bottom: 1.5rem;
  color: var(--primary-color);
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.modal-actions .btn {
  flex: 1;
}
</style>