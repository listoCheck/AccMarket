package com.example.accmarket.notification.service

import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.notification.models.DTO.NotificationCreateDTO
import com.example.accmarket.notification.models.Notification
import com.example.accmarket.notification.models.NotificationType
import com.example.accmarket.notification.repository.NotificationRepository
import com.example.accmarket.utils.mail.EmailService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val mailService: EmailService,
    private val userRepository: UserRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun send(
        userId: UUID,
        type: NotificationType,
        title: String,
        message: String
    ) {
        println("sending notification for user $userId")

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val notification = Notification(
            userId = userId,
            type = type,
            title = title,
            message = message
        )
        //notificationRepository.saveAndFlush(notification)

        try {
            user.email.takeIf { it.isNotBlank() }?.let { email ->
                println("EMAIL: $email")
                mailService.sendEmail(to = email, subject = title, text = message)
            }
        } catch (ex: Exception) {
            println("Ошибка при отправке email: ${ex.message}")
        }
    }

    fun sendForSystem(userId: UUID, type: NotificationType, title: String, message: String): Notification {
        val notification = Notification(
            userId = userId,
            type = type,
            title = title,
            message = message,
            createdAt = Date()
        )
        return notificationRepository.save(notification)
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
    @Transactional
    fun markAllAsRead(userId: UUID) {
        notificationRepository.markAllAsReadByUserId(userId)
    }
    fun create(dto: NotificationCreateDTO): Notification {
        val notification = Notification(
            userId = dto.userId,
            type = dto.type,
            title = dto.title,
            message = dto.message
        )
        return notificationRepository.save(notification)
    }

}
