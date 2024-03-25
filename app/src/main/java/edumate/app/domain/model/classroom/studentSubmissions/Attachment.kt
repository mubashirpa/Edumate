package edumate.app.domain.model.classroom.studentSubmissions

import edumate.app.domain.model.classroom.DriveFile
import edumate.app.domain.model.classroom.Link

data class Attachment(
    val driveFile: DriveFile? = null,
    val link: Link? = null,
)
