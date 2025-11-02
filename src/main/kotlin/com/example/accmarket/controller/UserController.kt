package com.example.accmarket.controller

import com.example.accmarket.models.Request
import com.example.accmarket.models.User
import com.example.accmarket.models.dto.UserDto
import com.example.accmarket.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/register")
    fun register(@RequestBody user: UserDto): Request {
        return userService.register(user.username, user.password)
    }

    @PostMapping("/login")
    fun login(@RequestBody user: UserDto): Request {
        return userService.login(user.username, user.password)
    }
}