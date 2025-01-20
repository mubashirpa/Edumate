package app.edumate.data.remote.dto.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val displayName: String? = null,
    val emailAddress: String? = null,
    val id: String? = null,
    val isVerified: Boolean? = null,
    val photoUrl: String? = null,
)
