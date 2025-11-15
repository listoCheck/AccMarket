package com.example.accmarket.auth.controller

import com.example.accmarket.utils.models.Response
import com.example.accmarket.auth.models.DTO.UserDto
import com.example.accmarket.auth.service.UserService
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
    fun register(@RequestBody user: UserDto): Response {
        return userService.register(user.username, user.password ?: "")
    }

    @PostMapping("/login")
    fun login(@RequestBody user: UserDto): Response {
        return userService.login(user.username, user.password ?: "")
    }

    @PostMapping("/logout")
    fun logout(@RequestBody user: UserDto): Response {
        return userService.logout(user.username, user.token ?: "")
    }

    @PostMapping("/update-token")
    fun updateToken(@RequestBody user: UserDto): Response {
        return userService.updateAccessToken(user.username, user.token ?: "")
    }
}