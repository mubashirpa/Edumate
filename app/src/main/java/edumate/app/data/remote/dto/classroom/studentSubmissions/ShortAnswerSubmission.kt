package edumate.app.data.remote.dto.classroom.studentSubmissions

import kotlinx.serialization.Serializable

@Serializable
data class ShortAnswerSubmission(
    val answer: String? = null,
)
