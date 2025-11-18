package com.example.accmarket.core.service

import AdvertisementResponseDTO
import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.auth.service.TokenService
import com.example.accmarket.core.models.Advertisement
import com.example.accmarket.core.models.DTO.AdvertisementDTO
import com.example.accmarket.core.models.DTO.DeleteAdvertisementDTO
import com.example.accmarket.core.models.Type
import com.example.accmarket.core.repository.AdvertisementRepository
import com.example.accmarket.utils.JWT.JwtProvider
import com.example.accmarket.utils.models.response.Response
import org.hibernate.query.Page.page
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.awt.print.Pageable
import java.util.Date
import java.util.UUID

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val jwtProvider: JwtProvider,
) {
    fun makeAdvertisement(request: AdvertisementDTO): Response {
        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 400, message = "User not found")

        if (!jwtProvider.verifyToken(request.token.toString()))
            return Response(code = 400, message = "Token not found or invalid")

        val adv = Advertisement(
            userId = user.id,
            title = request.title,
            text = request.text,
            cost = request.cost,
            checked = false,
            createdAt = Date(System.currentTimeMillis())
        )

        adv.type = Type(advertisement = adv, platform = request.platform, genre = request.genre)

        advertisementRepository.save(adv)
        return Response(code = 200, body = mapOf("token" to adv.id), message = "Advertisement is saved successfully")
    }


    fun editAdvertisement(request: AdvertisementDTO): Response {
        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 400, message = "User not found")

        if (!jwtProvider.verifyToken(request.token.toString()))
            return Response(code = 401, message = "Token not found or invalid")

        val adv = advertisementRepository.findById(request.advertisementId!!)
            .orElse(null) ?: return Response(code = 404, message = "Advertisement not found")

        if (adv.userId != user.id)
            return Response(code = 403, message = "You can't edit this advertisement")

        adv.title = request.title
        adv.text = request.text
        adv.cost = request.cost

        adv.type?.platform = request.platform
        adv.type?.genre = request.genre

        advertisementRepository.save(adv)
        return Response(code = 200, body = mapOf("token" to adv.id), message = "Advertisement updated successfully")
    }
    fun deleteAdvertisement(request: DeleteAdvertisementDTO): Response {
        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 400, message = "User not found")

        if (!jwtProvider.verifyToken(request.token.toString()))
            return Response(code = 400, message = "Token not found or invalid")

        val adv = advertisementRepository.findById(request.advertisementId)
            .orElse(null) ?: return Response(code = 404, message = "Advertisement not found")

        if (adv.userId != user.id)
            return Response(code = 403, message = "You can't edit this advertisement")
        adv.ended = true
        advertisementRepository.save(adv)
        return Response(code = 200, message = "Advertisement updated successfully")
    }

    fun getAdvertisements(
        userId: String? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "createdAt"
    ): Page<AdvertisementResponseDTO> {

        val pageable = PageRequest.of(page, size, Sort.by(sortBy).descending())

        val adsPage = if (userId != null) {
            val uuid = UUID.fromString(userId)
            advertisementRepository.findAllByUserId(uuid, pageable)
        } else {
            advertisementRepository.findAll(pageable)
        }

        return adsPage.map { AdvertisementResponseDTO.fromEntity(it) }
    }


}