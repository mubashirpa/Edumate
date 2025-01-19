package app.edumate.domain.model

data class User(
    val id: String? = null,
    val emailAddress: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isVerified: Boolean? = null,
)
