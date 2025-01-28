package app.edumate.domain.model.course

import app.edumate.domain.model.member.UserRole

data class Courses(
    val course: Course? = null,
    val role: UserRole? = null,
)
