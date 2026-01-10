package com.example.accmarket.core.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "game_account")
data class GameAccount(

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),

    @OneToOne
    @JoinColumn(name = "advertisement_id", nullable = false, unique = true)
    val advertisement: Advertisement,

    @Column(nullable = false)
    var login: String,

    @Column(nullable = false)
    var password: String
)
