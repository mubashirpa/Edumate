package app.edumate.data.remote.dto.users

import app.edumate.data.remote.dto.member.UserRoleDto
import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
    val role: UserRoleDto? = null,
    val user: UserDto? = null,
)
