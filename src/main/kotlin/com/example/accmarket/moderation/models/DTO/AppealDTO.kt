package com.example.accmarket.moderation.models.DTO

import com.example.accmarket.moderation.models.AppealStatus
import java.util.*

data class AppealCreateDTO(
    val advertisementId: UUID
)

data class AppealDecisionDTO(
    val appealId: UUID,
    val status: AppealStatus,
    val decision: String?
)
