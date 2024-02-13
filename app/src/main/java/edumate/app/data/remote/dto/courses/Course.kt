package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val alternateLink: String? = null,
    val calendarId: String? = null,
    val courseGroupEmail: String? = null,
    val courseMaterialSets: CourseMaterialSet? = null,
    val courseState: CourseState? = null,
    val creationTime: String? = null,
    val descriptionHeading: String? = null,
    val description: String? = null,
    val enrollmentCode: String? = null,
    val gradebookSettings: GradebookSettings? = null,
    val guardiansEnabled: Boolean? = null,
    val id: String? = null,
    val name: String? = null,
    val ownerId: String? = null,
    val room: String? = null,
    val section: String? = null,
    val teacherFolder: DriveFolder? = null,
    val teacherGroupEmail: String? = null,
    val updateTime: String? = null,
)
