package com.example.accmarket.core.controller

import AdvertisementResponseDTO
import com.example.accmarket.core.models.DTO.AdvertisementDTO
import com.example.accmarket.core.models.DTO.DeleteAdvertisementDTO
import com.example.accmarket.core.service.AdvertisementService
import com.example.accmarket.utils.models.response.Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/core")
class AdvertisementController(
    val advertisementService: AdvertisementService
) {

    @PostMapping("/make-advertisement")
    @Operation(
        summary = "Создание объявления",
        description = "Создает новое объявление и проверяет текст на запрещенные слова",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = AdvertisementDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Объявление успешно создано", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "400", description = "Объявление содержит запрещенные слова или пользователь/токен не найден", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun makeAdvertisement(@RequestBody request: AdvertisementDTO): Response {
        return advertisementService.makeAdvertisement(request)
    }

    @PatchMapping("/edit-advertisement")
    @Operation(
        summary = "Редактирование объявления",
        description = "Редактирует существующее объявление и проверяет текст на запрещенные слова",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = AdvertisementDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Объявление успешно обновлено", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "400", description = "Пользователь не найден или токен недействителен", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "403", description = "Нет доступа к редактированию объявления", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "404", description = "Объявление не найдено", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun editAdvertisement(@RequestBody request: AdvertisementDTO): Response {
        return advertisementService.editAdvertisement(request)
    }

    @PostMapping
    @Operation(
        summary = "Удаление объявления",
        description = "Помечает объявление как завершенное",
        requestBody = SwaggerRequestBody(
            required = true,
            content = [Content(schema = Schema(implementation = DeleteAdvertisementDTO::class))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Объявление успешно удалено", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "400", description = "Пользователь не найден или токен недействителен", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "403", description = "Нет доступа к удалению объявления", content = [Content(schema = Schema(implementation = Response::class))]),
            ApiResponse(responseCode = "404", description = "Объявление не найдено", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun deleteAdvertisement(@RequestBody request: DeleteAdvertisementDTO): Response {
        return advertisementService.deleteAdvertisement(request)
    }

    @GetMapping
    @Operation(
        summary = "Получение списка объявлений",
        description = "Возвращает страницу объявлений с фильтрацией по пользователю и сортировкой",
        responses = [
            ApiResponse(responseCode = "200", description = "Список объявлений успешно получен", content = [Content(schema = Schema(implementation = Page::class))])
        ]
    )
    fun getAdvertisements(
        @RequestParam(required = false) userId: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String
    ): Page<AdvertisementResponseDTO> {
        return advertisementService.getAdvertisements(userId, page, size, sortBy, rejected=false)
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Получение объявлений пользователя",
        description = "Возвращает страницу объявлений конкретного пользователя",
        responses = [
            ApiResponse(responseCode = "200", description = "Список объявлений пользователя успешно получен", content = [Content(schema = Schema(implementation = Page::class))]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [Content(schema = Schema(implementation = Response::class))])
        ]
    )
    fun getUserAdvertisements(@PathVariable userId: UUID): Page<AdvertisementResponseDTO> {
        return advertisementService.getUserAdvertisementsByUserId(userId)
    }

}
