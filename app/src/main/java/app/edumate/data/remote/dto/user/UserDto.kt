package app.edumate.data.remote.dto.user

import app.edumate.data.remote.dto.member.UserRoleDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("creation_time")
    val creationTime: String? = null,
    val email: String? = null,
    val id: String? = null,
    val name: String? = null,
    @SerialName("photo_url")
    val photoUrl: String? = null,
    val role: UserRoleDto? = null,
    @SerialName("update_time")
    val updateTime: String? = null,
)
