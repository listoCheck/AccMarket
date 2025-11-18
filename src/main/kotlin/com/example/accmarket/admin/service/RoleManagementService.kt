// src/main/kotlin/com/example/accmarket/rolemanagement/service/RoleManagementService.kt
package com.example.accmarket.rolemanagement.service

import com.example.accmarket.admin.models.DTO.AssignAdminRoleDTO
import com.example.accmarket.auth.models.User
import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.auth.service.TokenService
import com.example.accmarket.rolemanagement.models.AdminSecret
import com.example.accmarket.rolemanagement.repository.AdminSecretRepository
import com.example.accmarket.rolemanagement.models.DTO.RoleManagementDTO
import com.example.accmarket.utils.models.Response
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleManagementService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val adminSecretRepository: AdminSecretRepository
) {

    fun assignAdminRole(request: AssignAdminRoleDTO): Response {
        if (!tokenService.checkToken(request.token)) {
            return Response(code = 401, message = "Invalid token")
        }

        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 404, message = "User not found")

        if (!adminSecretRepository.isValidSecret(request.adminSecret)) {
            return Response(code = 403, message = "Invalid admin secret")
        }

        val updatedRoles = user.roles.toMutableSet().apply {
            add("ADMIN")
        }
        user.roles = updatedRoles
        userRepository.save(user)

        return Response(code = 200, message = "Admin role assigned successfully")
    }

    fun getUserRoles(request: RoleManagementDTO): Response {
        if (!tokenService.checkToken(request.token)) {
            return Response(code = 401, message = "Invalid token")
        }

        val currentUser = userRepository.findByUsername(request.username)
            ?: return Response(code = 404, message = "User not found")

        if (!hasRoleManagementAccess(currentUser)) {
            return Response(code = 403, message = "Insufficient permissions")
        }

        val targetUsername = request.targetUsername ?: request.username
        val targetUser = userRepository.findByUsername(targetUsername)
            ?: return Response(code = 404, message = "Target user not found")

        return Response(
            code = 200,
            body = mapOf(
                "username" to targetUser.username,
                "roles" to targetUser.roles
            ),
            message = "Roles retrieved successfully"
        )
    }

    fun updateUserRoles(request: RoleManagementDTO): Response {
        if (!tokenService.checkToken(request.token)) {
            return Response(code = 401, message = "Invalid token")
        }

        val currentUser = userRepository.findByUsername(request.username)
            ?: return Response(code = 404, message = "User not found")

        if (!hasRoleManagementAccess(currentUser)) {
            return Response(code = 403, message = "Insufficient permissions")
        }

        val targetUsername = request.targetUsername
            ?: return Response(code = 400, message = "Target username is required")

        val newRoles = request.roles
            ?: return Response(code = 400, message = "Roles are required")

        val targetUser = userRepository.findByUsername(targetUsername)
            ?: return Response(code = 404, message = "Target user not found")

        if (newRoles.isEmpty()) {
            return Response(code = 400, message = "User must have at least one role")
        }

        if (!currentUser.roles.contains("ADMIN") && newRoles.any { it == "ADMIN" }) {
            return Response(code = 403, message = "Only admins can assign ADMIN role")
        }

        targetUser.roles = newRoles
        userRepository.save(targetUser)

        return Response(code = 200, message = "Roles updated successfully")
    }

    fun getAllUsers(request: RoleManagementDTO): Response {
        if (!tokenService.checkToken(request.token)) {
            return Response(code = 401, message = "Invalid token")
        }

        val currentUser = userRepository.findByUsername(request.username)
            ?: return Response(code = 404, message = "User not found")

        if (!currentUser.roles.contains("ADMIN")) {
            return Response(code = 403, message = "Admin access required")
        }

        val users = userRepository.findAll().map { user ->
            mapOf(
                "id" to user.id,
                "username" to user.username,
                "roles" to user.roles
            )
        }

        return Response(
            code = 200,
            body = mapOf("users" to users),
            message = "Users retrieved successfully"
        )
    }

    private fun hasRoleManagementAccess(user: User): Boolean {
        return user.roles.any { it == "ADMIN" || it == "MODERATOR" }
    }

    fun initializeAdminSecret() {
        if (adminSecretRepository.count() == 0L) {
            val defaultSecret = AdminSecret(
                secretKey = System.getProperty("ADMIN_SECRET_DEFAULT"),
                description = "Default admin secret key"
            )
            adminSecretRepository.save(defaultSecret)
        }
    }
}