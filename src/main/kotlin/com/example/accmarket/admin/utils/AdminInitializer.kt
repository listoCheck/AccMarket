package com.example.accmarket.admin.utils


import com.example.accmarket.admin.models.Admin
import com.example.accmarket.auth.models.Token
import com.example.accmarket.auth.models.User
import com.example.accmarket.auth.repository.TokenRepository
import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.rolemanagement.repository.AdminSecretRepository
import com.example.accmarket.utils.JWT.JwtProvider
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class AdminInitializer(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val adminSecretRepository: AdminSecretRepository,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {

        if (userRepository.existsByUsername("admin")) {
            return
        }

        val admin = User(
            username = "admin",
            email = "artem.amuz@gmail.com",
            password = passwordEncoder.encode("123456"),
            roles = setOf("ADMIN")
        )

        val refreshToken = jwtProvider.createRefreshToken(
            "admin", listOf("ADMIN")
        )

        admin.token = Token(
            user = admin, refreshToken = refreshToken, refreshRequired = Date(
                System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 10
            ), isActive = true
        )

        userRepository.save(admin)

        val accessToken = jwtProvider.createAccessToken(
            "admin", listOf("ADMIN")
        )
        val adminRecord = Admin(
            userId = admin.id,
            isActive = true,
            description = "Assigned admin role",
        )
        adminSecretRepository.save(adminRecord)

        println("======================================")
        println(" ADMIN USER CREATED ")
        println(" username: admin")
        println(" password: 123456")
        println(" accessToken: $accessToken")
        println(" refreshToken: $refreshToken")
        println("======================================")
    }
}
