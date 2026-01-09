<template>
  <div class="balance">
    <h1>Баланс</h1>

    <div class="balance-card">
      <div class="balance-info">
        <h2>Текущий баланс</h2>
        <p class="balance-amount">{{ balance }} ₽</p>
      </div>

      <div class="balance-actions">
        <button @click="showDepositModal = true" class="btn btn-primary">
          Пополнить
        </button>
        <button @click="showWithdrawModal = true" class="btn btn-secondary">
          Вывести
        </button>
      </div>
    </div>

    <div v-if="error" class="alert alert-error">
      {{ error }}
    </div>

    <div v-if="success" class="alert alert-success">
      {{ success }}
    </div>

    <!-- Deposit Modal -->
    <div v-if="showDepositModal" class="modal-overlay" @click="showDepositModal = false">
      <div class="modal" @click.stop>
        <h3>Пополнение баланса</h3>
        <form @submit.prevent="handleDeposit">
          <div class="form-group">
            <label for="depositAmount">Сумма (₽)</label>
            <input
              id="depositAmount"
              v-model.number="depositAmount"
              type="number"
              required
              min="1"
              step="0.01"
              placeholder="Введите сумму"
            />
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Обработка...' : 'Пополнить' }}
            </button>
            <button type="button" @click="showDepositModal = false" class="btn btn-secondary">
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Withdraw Modal -->
    <div v-if="showWithdrawModal" class="modal-overlay" @click="showWithdrawModal = false">
      <div class="modal" @click.stop>
        <h3>Вывод средств</h3>
        <form @submit.prevent="handleWithdraw">
          <div class="form-group">
            <label for="withdrawAmount">Сумма (₽)</label>
            <input
              id="withdrawAmount"
              v-model.number="withdrawAmount"
              type="number"
              required
              min="1"
              step="0.01"
              :max="balance"
              placeholder="Введите сумму"
            />
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Обработка...' : 'Вывести' }}
            </button>
            <button type="button" @click="showWithdrawModal = false" class="btn btn-secondary">
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { balanceAPI } from '../api/balance'

export default {
  name: 'Balance',
  setup() {
    const authStore = useAuthStore()

    const balance = ref(0)
    const loading = ref(false)
    const error = ref('')
    const success = ref('')

    const showDepositModal = ref(false)
    const showWithdrawModal = ref(false)
    const depositAmount = ref(0)
    const withdrawAmount = ref(0)

    const fetchBalance = async () => {
      try {
        const response = await balanceAPI.get(authStore.userId)
        balance.value = response.data.amount || 0
      } catch (err) {
        error.value = 'Ошибка загрузки баланса'
      }
    }

    const handleDeposit = async () => {
      loading.value = true
      error.value = ''
      success.value = ''

      try {
        await balanceAPI.deposit({
          userId: authStore.userId,
          amount: depositAmount.value
        })

        success.value = `Баланс успешно пополнен на ${depositAmount.value} ₽`
        showDepositModal.value = false
        depositAmount.value = 0
        await fetchBalance()
        
        // Уведомляем Header об изменении баланса
        window.dispatchEvent(new CustomEvent('balance-updated'))
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка пополнения баланса'
      } finally {
        loading.value = false
      }
    }

    const handleWithdraw = async () => {
      if (withdrawAmount.value > balance.value) {
        error.value = 'Недостаточно средств на балансе'
        return
      }

      loading.value = true
      error.value = ''
      success.value = ''

      try {
        await balanceAPI.withdraw({
          userId: authStore.userId,
          amount: withdrawAmount.value
        })

        success.value = `Успешно выведено ${withdrawAmount.value} ₽`
        showWithdrawModal.value = false
        withdrawAmount.value = 0
        await fetchBalance()
        
        // Уведомляем Header об изменении баланса
        window.dispatchEvent(new CustomEvent('balance-updated'))
      } catch (err) {
        error.value = err.response?.data?.message || 'Ошибка вывода средств'
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchBalance()
    })

    return {
      balance,
      loading,
      error,
      success,
      showDepositModal,
      showWithdrawModal,
      depositAmount,
      withdrawAmount,
      handleDeposit,
      handleWithdraw
    }
  }
}
</script>

<style scoped>
.balance {
  padding: 2rem 0;
  max-width: 600px;
  margin: 0 auto;
}

.balance h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.balance-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.balance-info {
  text-align: center;
  margin-bottom: 2rem;
}

.balance-info h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.balance-amount {
  font-size: 3rem;
  font-weight: bold;
  color: #27ae60;
  margin: 0;
}

.balance-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  width: 90%;
  max-width: 400px;
}

.modal h3 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
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

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
}

.modal-actions {
  display: flex;
  gap: 1rem;
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