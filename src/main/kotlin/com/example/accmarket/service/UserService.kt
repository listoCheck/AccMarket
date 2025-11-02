package com.example.accmarket.service

import com.example.accmarket.models.Request
import com.example.accmarket.models.User
import com.example.accmarket.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.example.accmarket.utils.JWT.JwtProvider

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) {
    fun register(username: String, password: String): Request {
        if (userRepository.existsByUsername(username)) {
            return Request(code = 409, message = "This login already exists")
        }
        val token = jwtProvider.createToken(username, listOf("admin"))
        val user = User(username = username, password = passwordEncoder.encode(password), token = token)
        userRepository.save(user)
        return Request(code = 200, body = mapOf("token" to token), message = "Success")
    }


    fun login(username: String, password: String): Request {
        if (userRepository.existsByUsername(username)) {
            val user = userRepository.findByUsername(username)
            if (passwordEncoder.matches(password, user!!.password)) {
                val token = jwtProvider.createToken(username, listOf("admin"))
                userRepository.updateUserToken(user.id, token)
                return Request(code = 200, body = mapOf("token" to token), message = "Success")
            }
        }
        return Request(code = 400, message = "Incorrect login or password")
    }
}