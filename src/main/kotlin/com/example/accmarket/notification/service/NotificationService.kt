package com.example.accmarket.notification.service

import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.notification.models.Notification
import com.example.accmarket.notification.models.NotificationType
import com.example.accmarket.notification.repository.NotificationRepository
import com.example.accmarket.utils.mail.EmailService
import org.springframework.stereotype.Service
import java.util.*

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val mailService: EmailService,
    private val userRepository: UserRepository
) {

    fun send(
        userId: UUID,
        type: NotificationType,
        title: String,
        message: String
    ): Notification {

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val notification = notificationRepository.save(
            Notification(
                userId = userId,
                type = type,
                title = title,
                message = message
            )
        )

        try {
            val email = user.email
            if (!email.isNullOrBlank()) {
                mailService.sendEmail(
                    to = email,
                    subject = title,
                    text = message
                )
            }
        } catch (ex: Exception) {
        }

        return notification
    }

    fun getUserNotifications(userId: UUID): List<Notification> =
        notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)

    fun getUnread(userId: UUID): List<Notification> =
        notificationRepository.findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)

    fun markAsRead(notificationId: UUID, userId: UUID): Notification {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { IllegalArgumentException("Notification not found") }

        if (notification.userId != userId)
            throw IllegalStateException("Access denied")

        if (!notification.isRead) {
            notification.isRead = true
            notificationRepository.save(notification)
        }

        return notification
    }

    fun markAllAsRead(userId: UUID) {
        notificationRepository.markAllAsReadByUserId(userId)
    }
}
