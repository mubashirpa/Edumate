package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class CourseMaterial(
    val driveFile: DriveFile? = null,
    val form: Form? = null,
    val link: Link? = null,
    val youTubeVideo: YouTubeVideo? = null,
)
