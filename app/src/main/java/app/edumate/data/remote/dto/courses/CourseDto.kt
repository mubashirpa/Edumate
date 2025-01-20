package app.edumate.data.remote.dto.courses

import app.edumate.data.remote.dto.users.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val alternateLink: String? = null,
    val creationTime: Long? = null,
    val description: String? = null,
    val enrollmentCode: String? = null,
    val id: String? = null,
    val name: String? = null,
    val owner: UserDto? = null,
    val ownerId: String? = null,
    val room: String? = null,
    val section: String? = null,
    val subject: String? = null,
    val updateTime: Long? = null,
)
