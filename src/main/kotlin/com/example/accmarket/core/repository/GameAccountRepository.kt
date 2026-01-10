package com.example.accmarket.core.repository

import com.example.accmarket.core.models.GameAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GameAccountRepository : JpaRepository<GameAccount, UUID> {
    fun findByAdvertisementId(advertisementId: UUID): GameAccount?
}
