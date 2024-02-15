package edumate.app.data.remote.dto.classroom

import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val thumbnailUrl: String? = null,
    val title: String? = null,
    val url: String? = null,
)
