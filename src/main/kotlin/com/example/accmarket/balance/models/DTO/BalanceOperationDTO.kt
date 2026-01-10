package com.example.accmarket.balance.models.DTO


import java.math.BigDecimal
import java.util.*

data class BalanceOperationDTO(
    val userId: UUID,
    val amount: BigDecimal
)
