package com.example.accmarket.moderation.service


import com.example.accmarket.core.models.AdvertisementStatus as CoreAdvertisementStatus
import com.example.accmarket.core.repository.AdvertisementRepository
import com.example.accmarket.moderation.models.Appeal
import com.example.accmarket.moderation.models.AdvertisementStatus
import com.example.accmarket.moderation.models.DTO.AppealCreateDTO
import com.example.accmarket.moderation.models.DTO.AppealDecisionDTO
import com.example.accmarket.moderation.models.DTO.AppealResponseDTO
import com.example.accmarket.moderation.repository.AppealRepository
import com.example.accmarket.notification.models.NotificationType
import com.example.accmarket.notification.service.NotificationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AppealService(
    private val appealRepository: AppealRepository,
    private val advertisementRepository: AdvertisementRepository,
    private val notificationService: NotificationService
) {

    @Transactional
    fun create(dto: AppealCreateDTO): AppealResponseDTO {
        val ad = advertisementRepository.findById(dto.advertisementId)
            .orElseThrow { IllegalArgumentException("Advertisement not found") }

        if (ad.rejected != true)
            throw IllegalStateException("Appeal allowed only for rejected advertisements")

        val appeal = appealRepository.save(Appeal(advertisement = ad, reason = dto.reason))

        sendAfterCommit {
            notificationService.send(
                userId = ad.userId,
                type = NotificationType.APPEAL_CREATED,
                title = "Appeal submitted",
                message = "Your appeal for advertisement \"${ad.title}\" has been submitted and is under review."
            )
        }

        return appeal.toDTO()
    }

    @Transactional
    fun decide(dto: AppealDecisionDTO): AppealResponseDTO {
        val appealId = dto.appealId
        val appeal = appealRepository.findById(appealId)
            .orElseThrow { IllegalArgumentException("Appeal not found") }

        if (appeal.status != AdvertisementStatus.PENDING)
            throw IllegalStateException("Appeal already decided")

        appeal.status = dto.status
        appeal.decision = dto.decision
        appeal.decidedAt = Date()

        // Обновляем статус объявления в зависимости от решения по апелляции
        val advertisement = appeal.advertisement
        when (dto.status) {
            AdvertisementStatus.APPROVED -> {
                advertisement.status = CoreAdvertisementStatus.MODERATION_APPROVED
                advertisement.rejected = false
            }
            AdvertisementStatus.REJECTED -> {
                // Объявление остается отклоненным
                advertisement.status = CoreAdvertisementStatus.MODERATION_REJECTED
                advertisement.rejected = true
            }
            else -> {}
        }
        advertisementRepository.save(advertisement)

        val saved = appealRepository.save(appeal)

        sendAfterCommit {
            notificationService.send(
                userId = appeal.advertisement.userId,
                type = if (dto.status == AdvertisementStatus.APPROVED)
                    NotificationType.APPEAL_APPROVED
                else
                    NotificationType.APPEAL_REJECTED,
                title = "Appeal decision",
                message = if (dto.status == AdvertisementStatus.APPROVED)
                    "Your appeal for advertisement \"${advertisement.title}\" has been approved. Your advertisement is now published."
                else
                    dto.decision ?: "Your appeal has been rejected."
            )
        }

        return saved.toDTO()
    }


    fun getPending(): List<AppealResponseDTO> =
        appealRepository.findAllByStatus(AdvertisementStatus.PENDING)
            .map { it.toDTO() }

    private fun Appeal.toDTO() = AppealResponseDTO(
        id = this.id!!,
        advertisementId = this.advertisement.id,
        adTitle = this.advertisement.title,
        reason = this.reason,
        status = this.status,
        decision = this.decision,
        createdAt = this.createdAt,
        decidedAt = this.decidedAt
    )

    private fun sendAfterCommit(action: () -> Unit) {
        org.springframework.transaction.support.TransactionSynchronizationManager
            .registerSynchronization(object :
                org.springframework.transaction.support.TransactionSynchronization {
                override fun afterCommit() {
                    try { action() } catch (ex: Exception) { }
                }
            })
    }
}
