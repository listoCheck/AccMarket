package com.example.accmarket.moderation.models

import com.example.accmarket.admin.models.Admin
import com.example.accmarket.core.models.Advertisement
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "moderation")
data class Moderation(

    @Id
    @GeneratedValue
    val id: UUID? = null,


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    val admin: Admin,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id", nullable = false, unique = true)
    val advertisement: Advertisement,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val decision: ModerationDecision,

    @Column
    val comment: String? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    val decidedAt: Date = Date(),

    @Version
    var version: Long? = null
)
