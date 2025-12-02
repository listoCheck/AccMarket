package com.example.accmarket.admin.models
import com.example.accmarket.utils.Json.JsonbConverter
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "admin_secrets")
data class Admin(
    @Id
    @Column(columnDefinition = "UUID")
    val userId: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column
    var description: String? = null,

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonbConverter::class)
    var statistics: Map<String, Any> = emptyMap()
)