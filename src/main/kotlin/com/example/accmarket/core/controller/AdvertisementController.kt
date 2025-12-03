package com.example.accmarket.core.controller

import AdvertisementResponseDTO
import com.example.accmarket.core.models.DTO.AdvertisementDTO
import com.example.accmarket.core.models.DTO.DeleteAdvertisementDTO
import com.example.accmarket.core.service.AdvertisementService
import com.example.accmarket.utils.models.response.Response
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/core")
class AdvertisementController(
    val advertisementService: AdvertisementService
) {
    @PostMapping("/make-advertisement")
    fun makeAdvertisement(@RequestBody request: AdvertisementDTO, ): Response {
        return advertisementService.makeAdvertisement(request)
    }
    @PatchMapping("/edit-advertisement")
    fun editAdvertisement(@RequestBody request: AdvertisementDTO): Response {
        return advertisementService.editAdvertisement(request)
    }
    @DeleteMapping
    fun deleteAdvertisement(@RequestBody request: DeleteAdvertisementDTO): Response {
        return advertisementService.deleteAdvertisement(request)
    }

    @GetMapping
    fun getAdvertisements(
        @RequestParam(required = false) userId: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String
    ): Page<AdvertisementResponseDTO> {
        return advertisementService.getAdvertisements(userId, page, size, sortBy, rejected=false)
    }

    @GetMapping("/user/{userId}")
    fun getUserAdvertisements(@PathVariable userId: String): Page<AdvertisementResponseDTO> {
        return advertisementService.getUserAdvertisements(userId)
    }
}