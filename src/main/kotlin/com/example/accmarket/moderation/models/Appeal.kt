package com.example.accmarket.moderation.models

import com.example.accmarket.core.models.Advertisement
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "appeal")
data class Appeal(

    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id", nullable = false)
    val advertisement: Advertisement,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AppealStatus = AppealStatus.PENDING,

    @Column
    var decision: String? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    val createdAt: Date = Date(),

    @Temporal(TemporalType.TIMESTAMP)
    var decidedAt: Date? = null
)
