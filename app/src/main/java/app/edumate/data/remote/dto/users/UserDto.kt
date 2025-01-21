package app.edumate.data.remote.dto.users

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    val email: String? = null,
    val id: String? = null,
    val name: String? = null,
)
