package com.example.accmarket.notification.repository


import com.example.accmarket.notification.models.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {

    fun findAllByUserIdOrderByCreatedAtDesc(userId: UUID): List<Notification>

    fun findAllByUserIdAndIsReadFalse(userId: UUID): List<Notification>
}
