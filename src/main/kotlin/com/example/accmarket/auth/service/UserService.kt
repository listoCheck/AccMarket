package com.example.accmarket.auth.service

import com.example.accmarket.utils.models.response.Response
import com.example.accmarket.auth.models.Token
import com.example.accmarket.auth.models.User
import com.example.accmarket.auth.repository.TokenRepository
import com.example.accmarket.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.example.accmarket.utils.JWT.JwtProvider
import com.example.accmarket.utils.models.response.ResponseHandler

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
            return ResponseHandler.userAlreadyExists()
        }
        val user = User(username = username, password = passwordEncoder.encode(password))
        val refreshToken = jwtProvider.createRefreshToken(username, listOf("admin"))
        user.token = Token(
            user = user,
            refreshToken = refreshToken,
            refreshRequired = Date(System.currentTimeMillis() + 30 * 60 * 60 * 24 * 1000L)
        )
        userRepository.save(user)
        return ResponseHandler.success(body = mapOf("token" to refreshToken))
    }

    fun login(username: String, password: String): Response {
        val user = userRepository.findByUsername(username)
            ?: return ResponseHandler.userNotFound()

        return if (passwordEncoder.matches(password, user.password)) {
            val refreshToken = jwtProvider.createRefreshToken(username, listOf("USER"))
            val accessToken = jwtProvider.createAccessToken(username, listOf("USER"))
            tokenRepository.updateUserToken(user.id, refreshToken)
            ResponseHandler.success(body = mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
        } else {
            ResponseHandler.userNotFound()
        }
    }


    fun logout(username: String, token: String): Response {
        val user = userRepository.findByUsername(username)
            ?: return ResponseHandler.userNotFound()

        val tokenCheck = jwtProvider.verifyToken(token)
        if (!tokenCheck) return ResponseHandler.tokenNotFound()
        val tokenEntity = tokenRepository.findByUserId(user.id)
        if (tokenEntity.refreshToken != token) return ResponseHandler.invalidToken()

        tokenEntity.isActive = false
        tokenEntity.refreshRequired = Date(System.currentTimeMillis() + 30 * 60 * 60 * 24 * 1000L)
        tokenRepository.save(tokenEntity)

        return ResponseHandler.success()
    }

    fun updateAccessToken(username: String, token: String): Response {
        val user = userRepository.findByUsername(username)
            ?: return ResponseHandler.userNotFound()
        val tokenCheck = jwtProvider.verifyToken(token)
        if (!tokenCheck) return ResponseHandler.tokenNotFound()
        val tokenEntity = tokenRepository.findByUserId(user.id)
        if (tokenEntity.refreshToken != token) return ResponseHandler.invalidToken()
        val newAccessToken = jwtProvider.createAccessToken(username, listOf("USER"))
        return ResponseHandler.success(body = mapOf("accessToken" to newAccessToken))
    }

}