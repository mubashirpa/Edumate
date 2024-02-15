package edumate.app.data.remote.dto.classroom.courseWork

import kotlinx.serialization.Serializable

@Serializable
data class MultipleChoiceQuestion(
    val choices: List<String>? = null,
)
