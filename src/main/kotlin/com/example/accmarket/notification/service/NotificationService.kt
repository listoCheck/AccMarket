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

        user.email.let {
            mailService.sendEmail(
                to = it,
                subject = title,
                text = message
            )
        }

        return notification
    }

    fun getUserNotifications(userId: UUID): List<Notification> =
        notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)

    fun getUnread(userId: UUID): List<Notification> =
        notificationRepository.findAllByUserIdAndIsReadFalse(userId)

    fun markAsRead(notificationId: UUID): Notification {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { IllegalArgumentException("Notification not found") }

        notification.isRead = true
        return notificationRepository.save(notification)
    }

    fun markAllAsRead(userId: UUID) {
        val unread = notificationRepository.findAllByUserIdAndIsReadFalse(userId)
        unread.forEach { it.isRead = true }
        notificationRepository.saveAll(unread)
    }
}
