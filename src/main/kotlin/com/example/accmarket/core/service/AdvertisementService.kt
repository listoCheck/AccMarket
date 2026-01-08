package com.example.accmarket.core.service

import AdvertisementResponseDTO
import com.example.accmarket.auth.repository.UserRepository
import com.example.accmarket.auth.service.TokenService
import com.example.accmarket.core.models.Advertisement
import com.example.accmarket.core.models.DTO.AdvertisementDTO
import com.example.accmarket.core.models.DTO.DeleteAdvertisementDTO
import com.example.accmarket.core.models.Type
import com.example.accmarket.core.repository.AdvertisementRepository
import com.example.accmarket.notification.models.NotificationType
import com.example.accmarket.notification.service.NotificationService
import com.example.accmarket.utils.JWT.JwtProvider
import com.example.accmarket.utils.banwords.Banword
import com.example.accmarket.utils.models.response.Response
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date
import java.util.UUID

@Service
class AdvertisementService(
    private val advertisementRepository: AdvertisementRepository,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val jwtProvider: JwtProvider,
    private val banwordService: Banword,
    private val notificationService: NotificationService
) {

    @Transactional
    fun makeAdvertisement(request: AdvertisementDTO): Response {
        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 400, message = "User not found")

        if (!jwtProvider.verifyToken(request.token))
            return Response(code = 400, message = "Token not found or invalid")

        val bannedWords = banwordService.find("${request.title} ${request.text}")

        val adv = Advertisement(
            userId = user.id,
            title = request.title,
            text = request.text,
            cost = request.cost,
            rejected = bannedWords.isNotEmpty(),
            createdAt = Date()
        )

        adv.type = Type(
            advertisement = adv,
            platform = request.platform,
            genre = request.genre
        )

        advertisementRepository.save(adv)

        sendAfterCommit {
            notificationService.send(
                user.id,
                if (bannedWords.isNotEmpty())
                    NotificationType.MODERATION_REJECTED
                else
                    NotificationType.SYSTEM,
                "Advertisement created",
                if (bannedWords.isNotEmpty())
                    "Your advertisement contains banned words and was sent for moderation."
                else
                    "Your advertisement was successfully published."
            )
        }

        return if (bannedWords.isNotEmpty()) {
            Response(
                code = 400,
                body = mapOf("token" to adv.id),
                message = "Advertisement contains banned words: ${
                    bannedWords.distinct().joinToString(", ")
                }. Saved for admin review."
            )
        } else {
            Response(
                code = 200,
                body = mapOf("token" to adv.id),
                message = "Advertisement is saved successfully"
            )
        }
    }

    fun editAdvertisement(request: AdvertisementDTO): Response {
        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 400, message = "User not found")

        if (!jwtProvider.verifyToken(request.token))
            return Response(code = 401, message = "Token not found or invalid")

        val adv = advertisementRepository.findById(request.advertisementId!!)
            .orElse(null) ?: return Response(code = 404, message = "Advertisement not found")

        if (adv.userId != user.id)
            return Response(code = 403, message = "You can't edit this advertisement")

        val bannedWords = banwordService.find("${request.title} ${request.text}")

        adv.apply {
            rejected = bannedWords.isNotEmpty()
            title = request.title
            text = request.text
            cost = request.cost
            type?.platform = request.platform
            type?.genre = request.genre
        }

        advertisementRepository.save(adv)

        return if (bannedWords.isNotEmpty()) {
            Response(
                code = 400,
                body = mapOf("token" to adv.id),
                message = "Advertisement contains banned words: ${
                    bannedWords.distinct().joinToString(", ")
                }. Saved for admin review."
            )
        } else {
            Response(
                code = 200,
                body = mapOf("token" to adv.id),
                message = "Advertisement updated successfully"
            )
        }
    }

    @Transactional
    fun deleteAdvertisement(request: DeleteAdvertisementDTO): Response {
        val user = userRepository.findByUsername(request.username)
            ?: return Response(code = 400, message = "User not found")

        if (!jwtProvider.verifyToken(request.token))
            return Response(code = 400, message = "Token not found or invalid")

        val adv = advertisementRepository.findById(request.advertisementId)
            .orElse(null) ?: return Response(code = 404, message = "Advertisement not found")

        if (adv.userId != user.id)
            return Response(code = 403, message = "You can't edit this advertisement")

        adv.ended = true
        advertisementRepository.save(adv)

        sendAfterCommit {
            notificationService.send(
                user.id,
                NotificationType.SYSTEM,
                "Advertisement ended",
                "Your advertisement \"${adv.title}\" has been marked as ended."
            )
        }

        return Response(code = 200, message = "Advertisement updated successfully")
    }

    fun getAdvertisements(
        userId: String? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "createdAt",
        rejected: Boolean
    ): Page<AdvertisementResponseDTO> {

        val pageable = PageRequest.of(page, size, Sort.by(sortBy).descending())

        val adsPage = if (userId != null) {
            val uuid = UUID.fromString(userId)
            advertisementRepository.findAllByUserIdAndRejectedAndEnded(
                uuid,
                rejected,
                false,
                pageable
            )
        } else {
            advertisementRepository.findAllByRejectedAndEnded(
                rejected,
                false,
                pageable
            )
        }
        return adsPage.map { AdvertisementResponseDTO.fromEntity(it) }
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

    fun getUserAdvertisementsByUserName(
        userName: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "createdAt",
    ): Page<AdvertisementResponseDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(sortBy).descending())
        val user = userRepository.findByUsername(userName)
            ?: throw IllegalArgumentException("User not found")
        val adsPage = advertisementRepository.findAllByUserIdAndRejectedAndEnded(
            user.id,
            null,
            false,
            pageable
        )
        return adsPage.map { AdvertisementResponseDTO.fromEntity(it) }
    }

    fun getUserAdvertisementsByUserId(
        userId: UUID,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "createdAt",
    ): Page<AdvertisementResponseDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(sortBy).descending())
        val adsPage = advertisementRepository.findAllByUserIdAndRejectedAndEnded(
            userId,
            null,
            false,
            pageable
        )
        return adsPage.map { AdvertisementResponseDTO.fromEntity(it) }
    }

}
