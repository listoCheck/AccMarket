package com.example.accmarket.rolemanagement.models

import com.example.accmarket.auth.models.User
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "admin_secrets")
data class AdminSecret(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val secretKey: String,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column
    var description: String? = null,
)