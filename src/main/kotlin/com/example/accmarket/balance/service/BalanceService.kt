package com.example.accmarket.balance.service

import com.example.accmarket.balance.models.Balance
import com.example.accmarket.balance.models.DTO.BalanceOperationDTO
import com.example.accmarket.balance.repository.BalanceRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class BalanceService(
    private val balanceRepository: BalanceRepository
) {

    fun get(userId: UUID): Balance =
        balanceRepository.findById(userId)
            .orElseGet {
                balanceRepository.save(Balance(userId = userId))
            }

    fun deposit(dto: BalanceOperationDTO): Balance {
        require(dto.amount > BigDecimal.ZERO)

        val balance = get(dto.userId)
        balance.amount = balance.amount.add(dto.amount)
        balance.updatedAt = Date()

        return balanceRepository.save(balance)
    }

    fun withdraw(dto: BalanceOperationDTO): Balance {
        require(dto.amount > BigDecimal.ZERO)

        val balance = get(dto.userId)

        if (balance.amount < dto.amount)
            throw IllegalStateException("Insufficient balance")

        balance.amount = balance.amount.subtract(dto.amount)
        balance.updatedAt = Date()

        return balanceRepository.save(balance)
    }
}
