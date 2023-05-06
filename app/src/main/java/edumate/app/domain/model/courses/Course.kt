package edumate.app.domain.model.courses

import edumate.app.domain.model.user_profiles.UserProfile
import java.util.*

data class Course(
    val id: String = "",
    val name: String = "",
    val section: String? = null,
    val descriptionHeading: String? = null,
    val description: String? = null,
    val room: String? = null,
    val ownerId: String = "",
    val creationTime: Date? = null,
    val updateTime: Date? = null,
    val enrollmentCode: String = "",
    val courseState: CourseState = CourseState.COURSE_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    val courseGroupId: ArrayList<String> = arrayListOf(),
    val teacherGroupId: ArrayList<String> = arrayListOf(),
    val guardiansEnabled: Boolean = false,
    val calendarId: String = "",
    val gradeBookSettings: GradeBookSettings? = null,
    val creatorProfile: UserProfile? = null
)