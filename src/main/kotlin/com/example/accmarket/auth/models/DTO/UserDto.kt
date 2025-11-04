package com.example.accmarket.auth.models.DTO

data class UserDto(
    val username: String,
    val password: String?,
    val token: String?,
)