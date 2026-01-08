package com.example.accmarket.moderation.controller

import com.example.accmarket.moderation.models.DTO.ModerationDTO
import com.example.accmarket.moderation.service.ModerationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(
    name = "Moderation",
    description = "Advertisement moderation (admin)"
)
@RestController
@RequestMapping("/admin/moderation")
class ModerationController(
    private val moderationService: ModerationService
) {

    @Operation(
        summary = "Moderate advertisement",
        description = "Approve or reject advertisement after moderation"
    )
    @ApiResponse(responseCode = "200", description = "Moderation decision saved")
    @PostMapping
    fun moderate(@RequestBody dto: ModerationDTO) =
        moderationService.moderate(dto)

    @Operation(
        summary = "Get moderation result",
        description = "Returns moderation result for an advertisement"
    )
    @ApiResponse(responseCode = "200", description = "Moderation result retrieved")
    @GetMapping("/{advertisementId}")
    fun getByAdvertisement(@PathVariable advertisementId: UUID) =
        moderationService.getByAdvertisement(advertisementId)
}
