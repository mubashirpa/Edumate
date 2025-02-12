package app.edumate.domain.model.course

import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User

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
    val totalStudents: Int? = null,
    val updateTime: String? = null,
) {
    val joinLink = "$alternateLink?code=$enrollmentCode"
}
