package app.edumate.data.remote.dto.course

import app.edumate.data.remote.dto.member.UserRoleDto
import kotlinx.serialization.Serializable

@Serializable
data class CoursesDto(
    val course: CourseDto? = null,
    val role: UserRoleDto? = null,
)
