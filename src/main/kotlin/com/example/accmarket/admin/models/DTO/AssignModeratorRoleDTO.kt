package com.example.accmarket.admin.models.DTO

import java.util.UUID

data class AssignModeratorRoleDTO (
    val adminId: UUID,
    val adminToken: String,
    val moderatorId: UUID,
)