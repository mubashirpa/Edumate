package edumate.app.data.remote.dto.userProfiles

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val creationTime: String? = null,
    val emailAddress: String? = null,
    val id: String? = null,
    val name: Name? = null,
    val photoUrl: String? = null,
    val updateTime: String? = null,
    val verified: Boolean? = null,
)
