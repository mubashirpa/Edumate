package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class DriveFolder(
    val alternateLink: String? = null,
    val id: String? = null,
    val title: String? = null,
)
