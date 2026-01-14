package com.example.accmarket.notification.models.DTO

import com.example.accmarket.notification.models.NotificationType
import java.util.*

data class NotificationCreateDTO(
    val userId: UUID,
    val type: NotificationType,
    val title: String,
    val message: String
)
