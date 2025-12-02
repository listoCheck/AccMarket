package com.example.accmarket.rolemanagement.controller

import AdvertisementResponseDTO
import com.example.accmarket.admin.models.DTO.AssignAdminRoleDTO
import com.example.accmarket.admin.models.DTO.AssignModeratorRoleDTO
import com.example.accmarket.core.service.AdvertisementService
import com.example.accmarket.rolemanagement.models.DTO.RoleManagementDTO
import com.example.accmarket.rolemanagement.service.AdminService
import com.example.accmarket.utils.models.response.Response
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/roles")
class AdminController(
    private val roleManagementService: AdminService,
    private val advertisementService: AdvertisementService,
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

    @GetMapping("/get-advertisements")
    fun getAdvertisements(@RequestParam(required = false) userId: String?,
                          @RequestParam(defaultValue = "0") page: Int,
                          @RequestParam(defaultValue = "10") size: Int,
                          @RequestParam(defaultValue = "createdAt") sortBy: String
    ): Page<AdvertisementResponseDTO> {
        return advertisementService.getAdvertisements(userId, page, size, sortBy, rejected=true)
    }

    @PostMapping("/assign-moderator")
    fun assignModerator(@RequestBody request: AssignModeratorRoleDTO): Response {
        return roleManagementService.assignModeratorRole(request)
    }

}