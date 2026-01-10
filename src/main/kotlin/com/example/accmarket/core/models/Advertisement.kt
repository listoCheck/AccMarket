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
    var buyerId: UUID? = null,

    @Column
    var title: String,

    @Column
    var text: String,

    @Column
    var cost: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AdvertisementStatus = AdvertisementStatus.CREATED,

    @OneToOne(mappedBy = "advertisement", cascade = [CascadeType.ALL])
    var type: Type? = null,

    @Column
    var createdAt: Date,

    @Column
    var ended: Boolean = false
)
