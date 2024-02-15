package edumate.app.data.remote.dto.classroom.studentSubmissions

import kotlinx.serialization.Serializable

@Serializable
data class AssignmentSubmission(
    val attachments: List<Attachment>? = null,
)
