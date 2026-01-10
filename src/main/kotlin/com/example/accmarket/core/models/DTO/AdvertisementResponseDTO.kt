import com.example.accmarket.core.models.Advertisement
import com.example.accmarket.core.models.AdvertisementStatus
import java.util.UUID

data class AdvertisementResponseDTO(
    val id: UUID,
    val title: String,
    val text: String,
    val cost: Int,
    val platform: String?,
    val genre: String?,
    val status: AdvertisementStatus
) {
    companion object {
        fun fromEntity(ad: Advertisement) =
            AdvertisementResponseDTO(
                id = ad.id,
                title = ad.title,
                text = ad.text,
                cost = ad.cost,
                platform = ad.type?.platform,
                genre = ad.type?.genre,
                status = ad.status
            )
    }
}
