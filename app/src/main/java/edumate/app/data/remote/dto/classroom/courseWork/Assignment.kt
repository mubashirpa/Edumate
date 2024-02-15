package edumate.app.data.remote.dto.classroom.courseWork

import edumate.app.data.remote.dto.classroom.DriveFolder
import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    val studentWorkFolder: DriveFolder? = null,
)
