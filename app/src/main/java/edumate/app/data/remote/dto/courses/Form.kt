package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val formUrl: String? = null,
    val responseUrl: String? = null,
    val thumbnailUrl: String? = null,
    val title: String? = null,
)
