package app.edumate.domain.model.courses

import app.edumate.domain.model.User

data class Course(
    val alternateLink: String? = null,
    val creationTime: String? = null,
    val description: String? = null,
    val enrollmentCode: String? = null,
    val id: String? = null,
    val name: String? = null,
    val owner: User? = null,
    val ownerId: String? = null,
    val role: UserRole? = null,
    val room: String? = null,
    val section: String? = null,
    val subject: String? = null,
    val updateTime: String? = null,
)
