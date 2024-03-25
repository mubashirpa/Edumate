package edumate.app.data.remote.dto.classroom.studentSubmissions

import kotlinx.serialization.Serializable

@Serializable
data class ModifyAttachments(
    val addAttachments: List<Attachment>? = null,
)
