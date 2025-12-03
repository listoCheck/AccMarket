package com.example.accmarket.rolemanagement.controller

import AdvertisementResponseDTO
import com.example.accmarket.admin.models.DTO.AssignAdminRoleDTO
import com.example.accmarket.admin.models.DTO.AssignModeratorRoleDTO
import com.example.accmarket.core.service.AdvertisementService
import com.example.accmarket.rolemanagement.models.DTO.RoleManagementDTO
import com.example.accmarket.rolemanagement.service.AdminService
import com.example.accmarket.utils.models.response.Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/roles")
class AdminController(
    private val roleManagementService: AdminService,
    private val advertisementService: AdvertisementService,
) {

    @PostMapping("/assign-admin")
    @Operation(
        summary = "Назначить пользователя администратором",
        description = "Позволяет назначить пользователя администратором проекта",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = AssignAdminRoleDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Роль администратора успешно назначена", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный токен", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun assignAdminRole(@RequestBody request: AssignAdminRoleDTO): Response {
        return roleManagementService.assignAdminRole(request)
    }

    @GetMapping("/user")
    @Operation(
        summary = "Получить роли пользователя",
        description = "Возвращает роли указанного пользователя",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = RoleManagementDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Роли пользователя получены", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный токен", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun getUserRoles(@RequestBody request: RoleManagementDTO): Response {
        return roleManagementService.getUserRoles(request)
    }

    @PatchMapping("/update")
    @Operation(
        summary = "Обновить роли пользователя",
        description = "Обновляет роли указанного пользователя",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = RoleManagementDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Роли успешно обновлены", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "403", description = "Недостаточно прав", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun updateUserRoles(@RequestBody request: RoleManagementDTO): Response {
        return roleManagementService.updateUserRoles(request)
    }

    @GetMapping("/all-users")
    @Operation(
        summary = "Получить всех пользователей",
        description = "Возвращает список всех пользователей с их ролями",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = RoleManagementDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Список пользователей успешно получен", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный токен", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "403", description = "Доступ запрещен", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun getAllUsers(@RequestBody request: RoleManagementDTO): Response {
        return roleManagementService.getAllUsers(request)
    }

    @GetMapping("/get-advertisements")
    @Operation(
        summary = "Получить объявления пользователя",
        description = "Возвращает страницы объявлений пользователя",
        responses = [
            ApiResponse(responseCode = "200", description = "Список объявлений успешно получен", content = [Content(schema = Schema(implementation = AdvertisementResponseDTO::class))])
        ]
    )
    fun getAdvertisements(
        @RequestParam(required = false) userId: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String
    ): Page<AdvertisementResponseDTO> {
        return advertisementService.getAdvertisements(userId, page, size, sortBy, rejected = true)
    }

    @PostMapping("/assign-moderator")
    @Operation(
        summary = "Назначить пользователя модератором",
        description = "Позволяет назначить пользователя модератором проекта",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = AssignModeratorRoleDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Роль модератора успешно назначена", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "401", description = "Неверный токен", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "403", description = "Недостаточно прав", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun assignModerator(@RequestBody request: AssignModeratorRoleDTO): Response {
        return roleManagementService.assignModeratorRole(request)
    }
}
