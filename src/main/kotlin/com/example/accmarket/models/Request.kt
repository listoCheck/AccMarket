package com.example.accmarket.models

data class Request(
    val code: Int = 200,
    val body: Any? = null,
    val message: String = ""
)
