package com.example.accmarket.notification.repository


import com.example.accmarket.notification.models.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {

    fun findAllByUserIdOrderByCreatedAtDesc(userId: UUID): List<Notification>

    fun findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId: UUID): List<Notification>

    @Modifying
    @Query(
        """
        UPDATE Notification n 
        SET n.isRead = true 
        WHERE n.userId = :userId AND n.isRead = false
        """
    )
    fun markAllAsReadByUserId(userId: UUID): Int
}

