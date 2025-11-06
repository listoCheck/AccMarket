package com.example.accmarket.auth.controller

import com.example.accmarket.auth.models.DTO.UserDto
import com.example.accmarket.auth.service.UserService
import com.example.accmarket.utils.models.Response
import com.example.accmarket.utils.security.SecurityConfig
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTests @Autowired constructor(
    val mockMvc: MockMvc
) {

    @MockkBean
    lateinit var userService: UserService

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `register endpoint returns success`() {
        val user = UserDto(username = "testuser", password = "123456", token = null)
        val response = Response(code = 200, body = null, message = "User registered")

        every { userService.register(user.username, user.password ?: "") } returns response

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isOk() }
            jsonPath("$.code") { value(200) }
            jsonPath("$.message") { value("User registered") }
        }
    }

    @Test
    fun `login endpoint returns token`() {
        val user = UserDto(username = "testuser", password = "123456", token = null)
        val response = Response(code = 200, body = "Token123", message = "Login successful")

        every { userService.login(user.username, user.password ?: "") } returns response

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isOk() }
            jsonPath("$.code") { value(200) }
            jsonPath("$.body") { value("Token123") }
            jsonPath("$.message") { value("Login successful") }
        }
    }

    @Test
    fun `logout endpoint returns success`() {
        val user = UserDto(username = "testuser", password = null, token = "Token123")
        val response = Response(code = 200, body = null, message = "User logged out")

        every { userService.logout(user.username, user.token ?: "") } returns response

        mockMvc.post("/auth/logout") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isOk() }
            jsonPath("$.code") { value(200) }
            jsonPath("$.message") { value("User logged out") }
        }
    }
}
