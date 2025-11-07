package com.example.accmarket.rolemanagement.models.DTO

import java.util.UUID

data class RoleManagementDTO(
    val username: String,
    val token: UUID,
    val targetUsername: String? = null,
    val roles: Set<String>? = null,
    val adminSecret: String? = null
)

data class AssignAdminRoleDTO(
    val username: String,
    val token: UUID,
    val adminSecret: String
)