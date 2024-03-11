package edumate.app.data.remote.dto.classroom.teachers

import kotlinx.serialization.Serializable

@Serializable
data class TeachersDto(
    val nextPage: Int? = null,
    val teachers: List<Teacher>? = null,
)
