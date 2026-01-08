package com.example.accmarket.moderation.repository

import com.example.accmarket.moderation.models.Appeal
import com.example.accmarket.moderation.models.AdvertisementStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppealRepository : JpaRepository<Appeal, UUID> {

    fun findAllByStatus(status: AdvertisementStatus): List<Appeal>

    fun findAllByAdvertisementId(advertisementId: UUID): List<Appeal>
}
