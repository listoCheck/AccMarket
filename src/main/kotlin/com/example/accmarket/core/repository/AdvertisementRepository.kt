package com.example.accmarket.core.repository

import com.example.accmarket.core.models.Advertisement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AdvertisementRepository : JpaRepository<Advertisement, UUID> {
    fun findAllByUserIdAndRejected(userId: UUID, rejected: Boolean?, pageable: Pageable): Page<Advertisement>
    fun findAllByRejected(rejected: Boolean, pageable: Pageable): Page<Advertisement>
}
