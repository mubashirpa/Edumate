package app.edumate.data.remote.dto.courseWorks

import kotlinx.serialization.Serializable

@Serializable
data class MultipleChoiceQuestionDto(
    val choices: List<String>? = null,
)
