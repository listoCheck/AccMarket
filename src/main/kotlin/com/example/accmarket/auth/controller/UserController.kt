package com.example.accmarket.auth.controller

import com.example.accmarket.auth.models.DTO.UserDto
import com.example.accmarket.auth.service.UserService
import com.example.accmarket.utils.models.response.Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/register")
    @Operation(
        summary = "Регистрация пользователя",
        description = "Регистрация нового пользователя с отправкой приветственного письма",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = UserDto::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "400", description = "Пользователь или email уже существует", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun register(@RequestBody user: UserDto): Response {
        return userService.register(user.username, user.email ?: "", user.password ?: "")
    }

    @PostMapping("/login")
    @Operation(
        summary = "Авторизация пользователя",
        description = "Авторизация с выдачей access и refresh токенов",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = UserDto::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Авторизация успешна", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный логин или пароль", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun login(@RequestBody user: UserDto): Response {
        return userService.login(user.username, user.password ?: "")
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Выход пользователя",
        description = "Деактивация токена пользователя",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = UserDto::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Выход успешен", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный токен", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun logout(@RequestBody user: UserDto): Response {
        return userService.logout(user.username, user.token ?: "")
    }

    @PostMapping("/update-token")
    @Operation(
        summary = "Обновление access токена",
        description = "Выдача нового access токена по refresh токену",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = UserDto::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Новый access токен успешно создан", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный токен", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun updateToken(@RequestBody user: UserDto): Response {
        return userService.updateAccessToken(user.username, user.token ?: "")
    }
}
