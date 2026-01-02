package com.example.accmarket.moderation.service

import com.example.accmarket.core.repository.AdvertisementRepository
import com.example.accmarket.moderation.models.DTO.ModerationDTO
import com.example.accmarket.moderation.models.Moderation
import com.example.accmarket.moderation.models.ModerationDecision
import com.example.accmarket.moderation.repository.ModerationRepository
import com.example.accmarket.rolemanagement.repository.AdminSecretRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ModerationService(
    private val moderationRepository: ModerationRepository,
    private val adminRepository: AdminSecretRepository,
    private val advertisementRepository: AdvertisementRepository
) {

    fun moderate(dto: ModerationDTO): Moderation {
        val admin = adminRepository.findById(dto.adminId).orElseThrow { IllegalArgumentException("Admin not found") }

        val ad = advertisementRepository.findById(dto.advertisementId)
            .orElseThrow { IllegalArgumentException("Advertisement not found") }

        if (moderationRepository.existsByAdvertisementId(ad.id)) throw IllegalStateException("Advertisement already moderated")

        val moderation = Moderation(
            admin = admin, advertisement = ad, decision = dto.decision, comment = dto.comment
        )

        if (dto.decision == ModerationDecision.APPROVED) {
            ad.rejected = false
        }

        advertisementRepository.save(ad)
        return moderationRepository.save(moderation)
    }

    fun getByAdvertisement(advertisementId: UUID): Moderation? =
        moderationRepository.findByAdvertisementId(advertisementId)
}
