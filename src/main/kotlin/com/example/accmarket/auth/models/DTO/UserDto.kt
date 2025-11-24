package com.example.accmarket.auth.models.DTO

data class UserDto(
    val username: String,
    val email: String?,
    val password: String?,
    val token: String?,
)