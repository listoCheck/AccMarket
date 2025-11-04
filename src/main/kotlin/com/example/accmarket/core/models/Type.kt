package com.example.accmarket.core.models

import com.example.accmarket.auth.models.User
import jakarta.persistence.*
import java.util.*
@Entity
@Table(name = "types")
data class Type(
    @Id
    @Column(columnDefinition = "UUID")
    val advId: UUID? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "advertisement_id")
    val advertisement: Advertisement,

    @Column(nullable = false)
    var platform: String,

    @Column(nullable = false)
    var genre: String,
)