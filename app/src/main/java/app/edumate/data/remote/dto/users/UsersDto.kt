package app.edumate.data.remote.dto.users

import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
    val role: UserRoleDto? = null,
    val user: UserDto? = null,
)
