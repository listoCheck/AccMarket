package com.example.accmarket.moderation.models.DTO

import com.example.accmarket.moderation.models.Moderation
import com.example.accmarket.moderation.models.ModerationDecision
import java.util.*

data class ModerationDTO(
    val adminId: UUID,
    val advertisementId: UUID,
    val decision: ModerationDecision,
    val comment: String? = null
)


