package com.example.accmarket.moderation.controller

import com.example.accmarket.moderation.models.DTO.AppealCreateDTO
import com.example.accmarket.moderation.models.DTO.AppealDecisionDTO
import com.example.accmarket.moderation.service.AppealService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Appeals",
    description = "Appeals for rejected advertisements"
)
@RestController
@RequestMapping("/appeals")
class AppealController(
    private val appealService: AppealService
) {

    @Operation(
        summary = "Create appeal",
        description = "Creates an appeal for a rejected advertisement"
    )
    @ApiResponse(responseCode = "200", description = "Appeal created")
    @PostMapping
    fun create(@RequestBody dto: AppealCreateDTO) =
        appealService.create(dto)

    @Operation(
        summary = "Decide appeal",
        description = "Approve or reject an appeal (admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Appeal decided")
    @PostMapping("/decide")
    fun decide(@RequestBody dto: AppealDecisionDTO) =
        appealService.decide(dto)

    @Operation(
        summary = "Get pending appeals",
        description = "Returns all pending appeals (admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Pending appeals retrieved")
    @GetMapping("/pending")
    fun pending() =
        appealService.getPending()
}
