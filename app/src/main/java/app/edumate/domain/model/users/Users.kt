package app.edumate.domain.model.users

import app.edumate.domain.model.member.UserRole

data class Users(
    val role: UserRole? = null,
    val user: User? = null,
)
