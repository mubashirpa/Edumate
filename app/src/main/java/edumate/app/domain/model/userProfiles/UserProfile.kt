package edumate.app.domain.model.userProfiles

data class UserProfile(
    val emailAddress: String? = null,
    val id: String? = null,
    val name: Name? = null,
    val photoUrl: String? = null,
    val verified: Boolean? = null,
)
