package com.example.accmarket.core.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "game_accounts")
class GameAccount(

    @Id
    @Column(columnDefinition = "UUID")
    var id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "advertisement_id")
    var advertisement: Advertisement,

    @Column(nullable = false)
    var login: String,

    @Column(nullable = false)
    var password: String
)
