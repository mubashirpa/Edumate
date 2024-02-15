package edumate.app.data.remote.dto.classroom.courses

import kotlinx.serialization.Serializable

@Serializable
data class CoursesDto(
    val courses: List<Course>? = null,
    val nextPageToken: String? = null,
)
