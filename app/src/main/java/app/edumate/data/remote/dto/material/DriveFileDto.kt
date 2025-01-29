package app.edumate.data.remote.dto.material

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriveFileDto(
    @SerialName("alternate_link")
    val alternateLink: String? = null,
    val id: String? = null,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,
    val title: String? = null,
)
