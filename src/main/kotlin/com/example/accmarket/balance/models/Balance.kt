package com.example.accmarket.balance.models

import com.example.accmarket.balance.models.crypto.BalanceEncryptConverter
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "balance")
data class Balance(

    @Id
    @Column(columnDefinition = "UUID")
    val userId: UUID,

    @Convert(converter = BalanceEncryptConverter::class)
    @Column(nullable = false)
    var amount: BigDecimal = BigDecimal.ZERO,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    var updatedAt: Date = Date()
)
