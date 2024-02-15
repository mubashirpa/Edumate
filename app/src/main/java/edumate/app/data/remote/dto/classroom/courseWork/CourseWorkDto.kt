package edumate.app.data.remote.dto.classroom.courseWork

import kotlinx.serialization.Serializable

@Serializable
data class CourseWorkDto(
    val courseWork: List<CourseWork>? = null,
    val nextPageToken: String? = null,
)
