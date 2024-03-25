package edumate.app.data.remote.dto.classroom.studentSubmissions

import kotlinx.serialization.Serializable

@Serializable
data class StudentSubmissionsDto(
    val nextPage: Int? = null,
    val studentSubmissions: List<StudentSubmission>? = null,
)
