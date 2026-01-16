package com.example.accmarket.moderation.service

import com.example.accmarket.core.models.AdvertisementStatus
import com.example.accmarket.core.repository.AdvertisementRepository
import com.example.accmarket.moderation.models.DTO.ModerationDTO
import com.example.accmarket.moderation.models.Moderation
import com.example.accmarket.moderation.models.ModerationDecision
import com.example.accmarket.moderation.repository.ModerationRepository
import com.example.accmarket.rolemanagement.repository.AdminSecretRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ModerationService(
    private val moderationRepository: ModerationRepository,
    private val adminRepository: AdminSecretRepository,
    private val advertisementRepository: AdvertisementRepository
) {
    @Transactional
    fun moderate(dto: ModerationDTO): Moderation {
        val admin = adminRepository.findById(dto.adminId).orElseThrow { IllegalArgumentException("Admin not found") }

        val ad = advertisementRepository.findById(dto.advertisementId)
            .orElseThrow { IllegalArgumentException("Advertisement not found") }

        if (moderationRepository.existsByAdvertisementId(ad.id)) throw IllegalStateException("Advertisement already moderated")

        val moderation = Moderation(
            admin = admin, advertisement = ad, decision = dto.decision, comment = dto.comment
        )

        // Обновляем статус и флаг rejected в зависимости от решения модерации
        when (dto.decision) {
            ModerationDecision.APPROVED -> {
                ad.status = AdvertisementStatus.MODERATION_APPROVED
                ad.rejected = false
            }
            ModerationDecision.REJECTED -> {
                ad.status = AdvertisementStatus.MODERATION_REJECTED
                ad.rejected = true
            }
        }

        advertisementRepository.save(ad)
        return moderationRepository.save(moderation)
    }

    fun getByAdvertisement(advertisementId: UUID): Moderation =
        moderationRepository.findByAdvertisementId(advertisementId)!!
    data class ModerationResponseDTO(
        val id: UUID?,
        val advertisementId: UUID,
        val decision: ModerationDecision,
        val comment: String?,
        val adminId: UUID
    )
    fun toDTO(m: Moderation) = ModerationResponseDTO(
        id = m.id,
        advertisementId = m.advertisement.id,
        decision = m.decision,
        comment = m.comment,
        adminId = m.admin.userId
    )
}
