package edumate.app.data.remote.dto.userProfiles

import kotlinx.serialization.Serializable

@Serializable
data class Name(
    val firstName: String? = null,
    val fullName: String? = null,
    val lastName: String? = null,
)
