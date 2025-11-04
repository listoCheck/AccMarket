package com.example.accmarket.auth.service

import com.example.accmarket.auth.repository.TokenRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TokenService(
    val tokenRepository: TokenRepository
) {
    fun checkToken(token: UUID): Boolean {
        val token = tokenRepository.findByRefreshToken(token.toString())?: return false
        val notExpired = tokenRepository.isTokenActive(token.toString())
        val isActive = token.isActive
        return notExpired && isActive
    }
}