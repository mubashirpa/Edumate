package app.edumate.domain.model.member

data class Member(
    val courseId: String? = null,
    val joinedAt: String? = null,
    val role: UserRole? = null,
    val userId: String? = null,
)
