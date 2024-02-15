package edumate.app.data.remote.dto.classroom.courseWork

import kotlinx.serialization.Serializable

@Serializable
data class DueDate(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
)
