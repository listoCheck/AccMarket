package com.example.accmarket.core.models


import com.example.accmarket.auth.models.Token
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "advertisement")
data class Advertisement(
    @Id
    @Column(columnDefinition = "UUID")
    val id: UUID = UUID.randomUUID(),

    @Column(columnDefinition = "UUID")
    val userId: UUID,

    @Column
    var title: String,

    @Column
    var text: String,

    @Column
    var cost: Int,

    @OneToOne(mappedBy = "advertisement", cascade = [CascadeType.ALL], orphanRemoval = true)
    var type: Type? = null,

    @Column
    var checked: Boolean,

    @Column
    var createdAt: Date,

    @Column
    var ended: Boolean = false,
)