package com.example.accmarket.rolemanagement.controller

import com.example.accmarket.admin.models.DTO.AssignAdminRoleDTO
import com.example.accmarket.rolemanagement.models.DTO.RoleManagementDTO
import com.example.accmarket.rolemanagement.service.RoleManagementService
import com.example.accmarket.utils.models.Response
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/roles")
class RoleManagementController(
    private val roleManagementService: RoleManagementService
) {

    @PostMapping("/assign-admin")
    fun assignAdminRole(@RequestBody request: AssignAdminRoleDTO): Response {
        return roleManagementService.assignAdminRole(request)
    }

    @GetMapping("/user")
    fun getUserRoles(@RequestBody request: RoleManagementDTO): Response {
        return roleManagementService.getUserRoles(request)
    }

    @PutMapping("/update")
    fun updateUserRoles(@RequestBody request: RoleManagementDTO): Response {
        return roleManagementService.updateUserRoles(request)
    }

    @GetMapping("/all-users")
    fun getAllUsers(@RequestBody request: RoleManagementDTO): Response {
        return roleManagementService.getAllUsers(request)
    }
}