package edumate.app.domain.model

import java.util.*

data class Course(
    val alternateLink: String? = null,
    val courseState: CourseState? = null,
    val creationTime: Date? = null,
    val description: String? = null,
    val descriptionHeading: String? = null,
    val id: String? = null,
    val name: String = "",
    val ownerId: String? = null,
    val room: String? = null,
    val section: String? = null,
    val students: ArrayList<String>? = arrayListOf(),
    val subject: String? = null,
    val teachers: ArrayList<String>? = arrayListOf(),
    val updateTime: Date? = null
)