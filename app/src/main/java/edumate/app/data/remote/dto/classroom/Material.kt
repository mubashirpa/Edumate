package edumate.app.data.remote.dto.classroom

import kotlinx.serialization.Serializable

@Serializable
data class Material(
    val driveFile: DriveFile? = null,
    val link: Link? = null,
)
