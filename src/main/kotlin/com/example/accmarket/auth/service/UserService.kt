package com.example.accmarket.auth.service

import com.example.accmarket.utils.models.Response
import com.example.accmarket.auth.models.Token
import com.example.accmarket.auth.models.User
import com.example.accmarket.auth.repository.TokenRepository
import com.example.accmarket.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.example.accmarket.utils.JWT.JwtProvider
import java.util.Date

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val tokenRepository: TokenRepository
) {
    fun register(username: String, password: String): Response {
        if (userRepository.existsByUsername(username)) {
            return Response(code = 409, message = "This login already exists")
        }
        val user = User(username = username, password = passwordEncoder.encode(password))
        val refreshToken = jwtProvider.createToken(username, listOf("admin"))
        user.token = Token(
            user = user,
            refreshToken = refreshToken,
            refreshRequired = Date(System.currentTimeMillis() + 30 * 60 * 60 * 24 * 1000L)
        )
        userRepository.save(user)
        return Response(code = 200, body = mapOf("token" to refreshToken), message = "Success")
    }

    fun login(username: String, password: String): Response {
        val user = userRepository.findByUsername(username)
            ?: return Response(code = 400, message = "Incorrect login or password")

        return if (passwordEncoder.matches(password, user.password)) {
            val token = jwtProvider.createToken(username, listOf("admin"))
            tokenRepository.updateUserToken(user.id, token)
            Response(code = 200, body = mapOf("token" to token), message = "Success")
        } else {
            Response(code = 400, message = "Incorrect login or password")
        }
    }


    fun logout(username: String, token: String): Response {
        val user = userRepository.findByUsername(username)
            ?: return Response(code = 400, message = "User not found")

        val tokenEntity = tokenRepository.findByUserId(user.id)
            ?: return Response(code = 400, message = "Token not found")

        if (tokenEntity.refreshToken != token) {
            return Response(code = 403, message = "Invalid token")
        }

        tokenEntity.isActive = false
        tokenEntity.refreshRequired = Date(System.currentTimeMillis() + 30 * 60 * 60 * 24 * 1000L)
        tokenRepository.save(tokenEntity)

        return Response(code = 200, message = "Logout successful")
    }

}