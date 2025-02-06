package app.edumate.domain.model.user

import app.edumate.domain.model.member.UserRole

data class User(
    val email: String? = null,
    val id: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val role: UserRole? = null,
)
