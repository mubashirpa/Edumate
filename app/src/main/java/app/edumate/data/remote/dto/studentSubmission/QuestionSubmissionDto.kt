package app.edumate.data.remote.dto.studentSubmission

import kotlinx.serialization.Serializable

@Serializable
data class QuestionSubmissionDto(
    val answer: String? = null,
)
