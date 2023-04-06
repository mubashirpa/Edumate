package edumate.app.domain.model.courses

import java.util.*

data class Course(
    val alternateLink: String? = null,
    val courseState: CourseState = CourseState.COURSE_STATE_UNSPECIFIED,
    val creationTime: Date? = null,
    val description: String? = null,
    val descriptionHeading: String? = null,
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val room: String? = null,
    val section: String? = null,
    val students: ArrayList<String> = arrayListOf(),
    val subject: String? = null,
    val teachers: ArrayList<String> = arrayListOf(),
    val updateTime: Date? = null
)