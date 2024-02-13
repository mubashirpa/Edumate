package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeVideo(
    val alternateLink: String? = null,
    val id: String? = null,
    val thumbnailUrl: String? = null,
    val title: String? = null,
)
