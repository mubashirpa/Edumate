package app.edumate.domain.model

data class User(
    val displayName: String? = null,
    val emailAddress: String? = null,
    val id: String? = null,
    val isVerified: Boolean? = null,
    val photoUrl: String? = null,
)
