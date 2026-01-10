package com.example.accmarket.core.repository

import com.example.accmarket.core.models.Advertisement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AdvertisementRepository : JpaRepository<Advertisement, UUID> {

    fun findAllByUserIdAndRejectedAndEnded(
        userId: UUID,
        rejected: Boolean?,
        ended: Boolean,
        pageable: Pageable
    ): Page<Advertisement>

    fun findAllByRejectedAndEnded(
        rejected: Boolean,
        ended: Boolean,
        pageable: Pageable
    ): Page<Advertisement>
    
    fun findAllByUserIdAndEnded(
        userId: UUID,
        ended: Boolean,
        pageable: Pageable
    ): Page<Advertisement>
}
