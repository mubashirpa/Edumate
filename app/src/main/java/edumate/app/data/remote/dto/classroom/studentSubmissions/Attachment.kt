package edumate.app.data.remote.dto.classroom.studentSubmissions

import edumate.app.data.remote.dto.classroom.DriveFile
import edumate.app.data.remote.dto.classroom.Link
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val driveFile: DriveFile? = null,
    val link: Link? = null,
)
