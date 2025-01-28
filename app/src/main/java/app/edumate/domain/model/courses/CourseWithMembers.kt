package app.edumate.domain.model.courses

import app.edumate.domain.model.member.Member
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.users.User

data class CourseWithMembers(
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
    val members: List<Member>? = null,
)
