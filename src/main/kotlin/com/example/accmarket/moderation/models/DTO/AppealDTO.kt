package com.example.accmarket.moderation.models.DTO

import com.example.accmarket.moderation.models.AdvertisementStatus
import java.util.*

data class AppealCreateDTO(
    val advertisementId: UUID
)

data class AppealDecisionDTO(
    val appealId: UUID,
    val status: AdvertisementStatus,
    val decision: String?
)

data class AppealResponseDTO(
    val id: UUID,
    val advertisementId: UUID,
    val adTitle: String,
    val status: AdvertisementStatus,
    val decision: String?,
    val createdAt: Date,
    val decidedAt: Date?
)
