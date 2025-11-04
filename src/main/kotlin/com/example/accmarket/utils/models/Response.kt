package com.example.accmarket.utils.models

data class Response(
    val code: Int = 200,
    val body: Any? = null,
    val message: String = ""
)