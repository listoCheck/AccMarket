package com.example.accmarket.notification.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "notification")
data class Notification(

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = "UUID", nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "text")
    val message: String,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    val createdAt: Date = Date()
)