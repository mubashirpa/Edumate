package edumate.app.data.remote.dto.classroom.students

import kotlinx.serialization.Serializable

@Serializable
data class StudentsDto(
    val nextPage: Int? = null,
    val students: List<Student>? = null,
)
