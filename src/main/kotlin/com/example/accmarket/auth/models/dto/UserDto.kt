package com.example.accmarket.auth.models.dto

data class UserDto(
    val username: String,
    val password: String?,
    val token: String?,
)