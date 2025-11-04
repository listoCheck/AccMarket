package com.example.accmarket.core.models.DTO

import java.util.UUID

data class AdvertisementDTO(
    val username: String,
    val token: UUID,
    val title: String,
    val text: String,
    val cost: Int,
    val platform: String,
    val genre: String,
    val advertisementId : UUID?,
)