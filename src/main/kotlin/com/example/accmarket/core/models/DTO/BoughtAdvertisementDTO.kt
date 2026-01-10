package com.example.accmarket.core.models.DTO

import java.util.UUID

data class BoughtAdvertisementDTO(
    val advertisementId: UUID,
    val title: String,
    val login: String,
    val password: String
)
