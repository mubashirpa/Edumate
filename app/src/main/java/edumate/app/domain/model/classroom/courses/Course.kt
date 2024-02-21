package edumate.app.domain.model.classroom.courses

import edumate.app.domain.model.classroom.students.Student
import edumate.app.domain.model.userProfiles.UserProfile

data class Course(
    val alternateLink: String? = null,
    val courseState: CourseState? = null,
    val creationTime: String? = null,
    val description: String? = null,
    val id: String? = null,
    val name: String? = null,
    val owner: UserProfile? = null,
    val ownerId: String? = null,
    val photoUrl: String? = null,
    val room: String? = null,
    val section: String? = null,
    val students: List<Student>? = null,
    val subject: String? = null,
    val updateTime: String? = null,
)
