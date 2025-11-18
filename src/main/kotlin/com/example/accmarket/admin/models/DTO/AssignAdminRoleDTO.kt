package com.example.accmarket.admin.models.DTO

import java.util.UUID

data class AssignAdminRoleDTO(
    val username: String,
    val token: UUID,
    val adminSecret: String
)