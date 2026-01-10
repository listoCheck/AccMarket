package com.example.accmarket.balance.repository

import com.example.accmarket.balance.models.Balance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BalanceRepository : JpaRepository<Balance, UUID>
