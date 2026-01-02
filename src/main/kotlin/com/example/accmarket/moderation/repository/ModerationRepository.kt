package com.example.accmarket.moderation.repository

import com.example.accmarket.moderation.models.Moderation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ModerationRepository : JpaRepository<Moderation, UUID> {

    fun existsByAdvertisementId(advertisementId: UUID): Boolean

    fun findByAdvertisementId(advertisementId: UUID): Moderation?
}
