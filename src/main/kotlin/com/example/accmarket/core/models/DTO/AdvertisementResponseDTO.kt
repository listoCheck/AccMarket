import com.example.accmarket.core.models.Advertisement
import java.util.UUID

data class AdvertisementResponseDTO(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val text: String,
    val cost: Int,
    val platform: String?,
    val genre: String?
) {
    companion object {
        fun fromEntity(ad: Advertisement): AdvertisementResponseDTO {
            return AdvertisementResponseDTO(
                id = ad.id,
                userId = ad.userId,
                title = ad.title,
                text = ad.text,
                cost = ad.cost,
                platform = ad.type?.platform,
                genre = ad.type?.genre
            )
        }
    }
}
