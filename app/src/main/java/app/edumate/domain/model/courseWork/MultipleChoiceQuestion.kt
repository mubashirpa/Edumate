package app.edumate.domain.model.courseWork

import kotlinx.serialization.Serializable

@Serializable
data class MultipleChoiceQuestion(
    val choices: List<String>? = null,
)
