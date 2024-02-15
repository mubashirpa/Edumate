package edumate.app.data.remote.dto.classroom.students

import kotlinx.serialization.Serializable

@Serializable
data class StudentsDto(
    val nextPageToken: String? = null,
    val students: List<Student>? = null,
)
