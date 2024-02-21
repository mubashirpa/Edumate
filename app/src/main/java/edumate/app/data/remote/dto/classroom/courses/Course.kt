package edumate.app.data.remote.dto.classroom.courses

import edumate.app.data.remote.dto.classroom.students.Student
import edumate.app.data.remote.dto.classroom.teachers.Teacher
import edumate.app.data.remote.dto.userProfiles.UserProfile
import kotlinx.serialization.Serializable

@Serializable
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
    val teachers: List<Teacher>? = null,
    val updateTime: String? = null,
)
