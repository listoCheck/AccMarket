package com.example.accmarket.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: Set<String> = setOf("USER"),

    @Column(name = "token", length = 1000)
    var token: String
)
