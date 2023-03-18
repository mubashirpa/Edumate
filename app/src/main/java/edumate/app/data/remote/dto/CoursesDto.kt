package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.courses.CourseState
import java.util.ArrayList
import java.util.Date

data class CoursesDto(
    val alternateLink: String? = null,
    val courseState: CourseState? = null,
    @ServerTimestamp val creationTime: Date? = null,
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
    @ServerTimestamp val updateTime: Date? = null
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.ALTERNATE_LINK to alternateLink,
            FirebaseConstants.Firestore.COURSE_STATE to courseState,
            FirebaseConstants.Firestore.CREATION_TIME to (
                creationTime
                    ?: FieldValue.serverTimestamp()
                ),
            FirebaseConstants.Firestore.DESCRIPTION to description,
            FirebaseConstants.Firestore.DESCRIPTION_HEADING to descriptionHeading,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.NAME to name,
            FirebaseConstants.Firestore.OWNER_ID to ownerId,
            FirebaseConstants.Firestore.ROOM to room,
            FirebaseConstants.Firestore.SECTION to section,
            FirebaseConstants.Firestore.STUDENTS to students,
            FirebaseConstants.Firestore.SUBJECT to subject,
            FirebaseConstants.Firestore.TEACHERS to teachers,
            FirebaseConstants.Firestore.UPDATE_TIME to FieldValue.serverTimestamp()
        )
    }
}