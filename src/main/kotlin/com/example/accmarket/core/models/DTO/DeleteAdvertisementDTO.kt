package com.example.accmarket.core.models.DTO

import java.util.UUID

data class DeleteAdvertisementDTO(
    val username: String,
    val token: String,
    val advertisementId : UUID,
)