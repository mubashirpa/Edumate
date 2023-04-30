package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.courses.CourseState
import edumate.app.domain.model.courses.GradeBookSettings
import java.util.ArrayList
import java.util.Date

data class CourseDto(
    val id: String = "",
    val name: String = "",
    val section: String? = null,
    val descriptionHeading: String? = null,
    val description: String? = null,
    val room: String? = null,
    val ownerId: String = "",
    @ServerTimestamp
    val creationTime: Date? = null,
    @ServerTimestamp
    val updateTime: Date? = null,
    val enrollmentCode: String = "",
    val courseState: CourseState = CourseState.COURSE_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    val courseGroupId: ArrayList<String> = arrayListOf(),
    val teacherGroupId: ArrayList<String> = arrayListOf(),
    val guardiansEnabled: Boolean = false,
    val calendarId: String = "",
    val gradeBookSettings: GradeBookSettings? = null
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.NAME to name,
            FirebaseConstants.Firestore.SECTION to section,
            FirebaseConstants.Firestore.DESCRIPTION_HEADING to descriptionHeading,
            FirebaseConstants.Firestore.DESCRIPTION to description,
            FirebaseConstants.Firestore.ROOM to room,
            FirebaseConstants.Firestore.OWNER_ID to ownerId,
            FirebaseConstants.Firestore.CREATION_TIME to (
                creationTime
                    ?: FieldValue.serverTimestamp()
                ),
            FirebaseConstants.Firestore.UPDATE_TIME to FieldValue.serverTimestamp(),
            FirebaseConstants.Firestore.ENROLLMENT_CODE to enrollmentCode,
            FirebaseConstants.Firestore.COURSE_STATE to courseState,
            FirebaseConstants.Firestore.ALTERNATE_LINK to alternateLink,
            FirebaseConstants.Firestore.COURSE_GROUP_ID to courseGroupId,
            FirebaseConstants.Firestore.TEACHER_GROUP_ID to teacherGroupId,
            FirebaseConstants.Firestore.GUARDIANS_ENABLED to guardiansEnabled,
            FirebaseConstants.Firestore.CALENDAR_ID to calendarId,
            FirebaseConstants.Firestore.GRADE_BOOK_SETTINGS to gradeBookSettings
        )
    }
}