package edumate.app.data.remote.dto.classroom.studentSubmissions

import kotlinx.serialization.Serializable

@Serializable
data class MultipleChoiceSubmission(
    val answer: String? = null,
)
