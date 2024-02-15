package edumate.app.data.remote.dto.classroom.studentSubmissions

import edumate.app.data.remote.dto.classroom.Link
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val link: Link? = null,
)
