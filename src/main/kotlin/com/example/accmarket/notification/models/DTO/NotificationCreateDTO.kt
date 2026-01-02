package com.example.accmarket.notification.models.DTO


import java.util.*

data class NotificationCreateDTO(
    val userId: UUID,
    val title: String,
    val message: String
)
