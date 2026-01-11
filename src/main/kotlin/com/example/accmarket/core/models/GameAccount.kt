package com.example.accmarket.core.models

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name = "game_accounts")
data class GameAccount(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID? = null,

    @OneToOne
    @JoinColumn(name = "advertisement_id")
    var advertisement: Advertisement,

    var login: String,
    var password: String
)

