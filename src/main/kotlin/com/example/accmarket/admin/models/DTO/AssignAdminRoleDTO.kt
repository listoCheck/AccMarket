package com.example.accmarket.admin.models.DTO

data class AssignAdminRoleDTO(
    val username: String,
    val token: String,
    val adminSecret: String,
    val email: String
)