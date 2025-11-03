package com.example.accmarket.auth.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tokens")
data class Token(
    @Id
    @Column(columnDefinition = "UUID")
    val userId: UUID? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "refresh_token", columnDefinition = "TEXT", nullable = false, length = 1024)
    var refreshToken: String,

    @Column(name = "refresh_required", nullable = false)
    var refreshRequired: Boolean = false,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
)
