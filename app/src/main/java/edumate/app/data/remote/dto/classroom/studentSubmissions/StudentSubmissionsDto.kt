package edumate.app.data.remote.dto.classroom.studentSubmissions

import kotlinx.serialization.Serializable

@Serializable
data class StudentSubmissionsDto(
    val nextPageToken: String? = null,
    val studentSubmissions: List<StudentSubmission>? = null,
)
