package app.edumate.data.remote.dto.material

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDto(
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,
    val title: String? = null,
    val url: String? = null,
)
