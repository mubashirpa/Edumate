package app.edumate.data.remote.dto.material

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MaterialDto(
    @SerialName("drive_file")
    val driveFile: DriveFileDto? = null,
    val link: LinkDto? = null,
)
