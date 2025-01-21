package app.edumate.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class CoursesDto(
    val course: CourseDto? = null,
)
