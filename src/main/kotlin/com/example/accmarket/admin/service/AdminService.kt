package com.example.accmarket.rolemanagement.service

import com.example.accmarket.admin.models.DTO.AssignAdminRoleDTO
import com.example.accmarket.admin.models.DTO.AssignModeratorRoleDTO
import com.example.accmarket.auth.models.User
import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.auth.service.TokenService
import com.example.accmarket.rolemanagement.repository.AdminSecretRepository
import com.example.accmarket.rolemanagement.models.DTO.RoleManagementDTO
import com.example.accmarket.utils.JWT.JwtProvider
import com.example.accmarket.utils.mail.EmailService
import com.example.accmarket.utils.models.response.Response
import com.example.accmarket.utils.models.response.ResponseHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val adminSecretRepository: AdminSecretRepository,
    private val jwtProvider: JwtProvider,
    private val emailService: EmailService,
) {

    @Value("\${ADMIN_SECRET_DEFAULT}")
    private lateinit var secret: String

    @Transactional
    fun assignAdminRole(request: AssignAdminRoleDTO): Response {

        if (!jwtProvider.verifyToken(request.token.toString())) return ResponseHandler.invalidToken()

        val user = userRepository.findByUsername(request.username)
            ?: return ResponseHandler.userNotFound()

        if (request.adminSecret != secret) {
            return ResponseHandler.invalidAdminSecret()
        }

        user.roles = user.roles.toMutableSet().apply { add("ADMIN") }
        userRepository.save(user)

        sendAfterCommit {
            emailService.sendEmail(
                to = user.email,
                subject = "AccMarket",
                text = """
                    Привет, ${user.username}!
                    
                    Теперь вы стали админом проекта, вы можете модерировать объявления и блокировать пользователей.
                """.trimIndent()
            )
        }

        return Response(code = 200, message = "Admin role assigned successfully")
    }

    fun getUserRoles(request: RoleManagementDTO): Response {
        if (!jwtProvider.verifyToken(request.token.toString())) return ResponseHandler.invalidToken()

        val currentUser = userRepository.findByUsername(request.username)
            ?: return ResponseHandler.userNotFound()

        if (!hasRoleManagementAccess(currentUser)) return ResponseHandler.insufficientPermissions()

        val targetUsername = request.targetUsername ?: request.username
        val targetUser = userRepository.findByUsername(targetUsername)
            ?: return ResponseHandler.targetUserNotFound()

        return ResponseHandler.success(
            body = mapOf("username" to targetUser.username, "roles" to targetUser.roles)
        )
    }

    @Transactional
    fun updateUserRoles(request: RoleManagementDTO): Response {
        if (!jwtProvider.verifyToken(request.token.toString())) return ResponseHandler.invalidToken()

        val currentUser = userRepository.findByUsername(request.username)
            ?: return ResponseHandler.userNotFound()

        if (!hasRoleManagementAccess(currentUser)) return ResponseHandler.insufficientPermissions()

        val targetUsername = request.targetUsername
            ?: return Response(code = 400, message = "Target username is required")

        val newRoles = request.roles
            ?: return Response(code = 400, message = "Roles are required")

        val targetUser = userRepository.findByUsername(targetUsername)
            ?: return Response(code = 404, message = "Target user not found")

        if (newRoles.isEmpty()) return Response(code = 400, message = "User must have at least one role")

        if (!currentUser.roles.contains("ADMIN") && newRoles.contains("ADMIN")) {
            return Response(code = 403, message = "Only admins can assign ADMIN role")
        }

        targetUser.roles = newRoles
        userRepository.save(targetUser)

        return Response(code = 200, message = "Roles updated successfully")
    }

    fun getAllUsers(request: RoleManagementDTO): Response {
        if (!tokenService.checkToken(request.token)) return Response(code = 401, message = "Invalid token")

        val currentUser = userRepository.findByUsername(request.username)
            ?: return Response(code = 404, message = "User not found")

        if (!currentUser.roles.contains("ADMIN")) return Response(code = 403, message = "Admin access required")

        val users = userRepository.findAll().map { user ->
            mapOf(
                "id" to user.id,
                "username" to user.username,
                "roles" to user.roles
            )
        }

        return ResponseHandler.success(body = mapOf("users" to users))
    }

    private fun hasRoleManagementAccess(user: User): Boolean {
        return user.roles.any { it == "ADMIN" || it == "MODERATOR" }
    }

    @Transactional
    fun assignModeratorRole(request: AssignModeratorRoleDTO): Response {
        if (!jwtProvider.verifyToken(request.adminToken)) return ResponseHandler.invalidToken()

        val adminUser = userRepository.findById(request.adminId).orElse(null)
            ?: return ResponseHandler.userNotFound()

        if (!hasRoleManagementAccess(adminUser)) return ResponseHandler.invalidToken()

        val targetUser = userRepository.findById(UUID.fromString(request.moderatorId.toString()))
            .orElse(null) ?: return ResponseHandler.targetUserNotFound()

        targetUser.roles = targetUser.roles.toMutableSet().apply { add("MODERATOR") }
        userRepository.save(targetUser)

        sendAfterCommit {
            emailService.sendEmail(
                to = targetUser.email,
                subject = "AccMarket",
                text = """
                    Привет, ${targetUser.username}!
                    
                    Теперь вы стали модератором проекта.
                """.trimIndent()
            )
        }

        return Response(code = 200, message = "Moderator role assigned successfully")
    }

    private fun sendAfterCommit(action: () -> Unit) {
        org.springframework.transaction.support.TransactionSynchronizationManager
            .registerSynchronization(object :
                org.springframework.transaction.support.TransactionSynchronization {

                override fun afterCommit() {
                    try {
                        action()
                    } catch (_: Exception) {
                    }
                }
            })
    }
}
