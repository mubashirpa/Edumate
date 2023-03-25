package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.course_work.CourseWorkState
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.course_work.Material
import java.util.Date

data class CourseWorkDto(
    val courseId: String = "",
    val id: String = "",
    var title: String = "",
    var description: String? = null,
    var materials: List<Material> = listOf(),
    var state: CourseWorkState = CourseWorkState.COURSE_WORK_STATE_UNSPECIFIED,
    @ServerTimestamp
    val creationTime: Date? = null,
    @ServerTimestamp
    val updateTime: Date? = null,
    var dueTime: Date? = null,
    var scheduledTime: Date? = null,
    var maxPoints: Int = 0,
    val workType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED,
    val creatorUserId: String = ""
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.COURSE_ID to courseId,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.TITLE to title,
            FirebaseConstants.Firestore.DESCRIPTION to description,
            FirebaseConstants.Firestore.MATERIALS to materials,
            FirebaseConstants.Firestore.STATE to state,
            FirebaseConstants.Firestore.CREATION_TIME to creationTime,
            FirebaseConstants.Firestore.UPDATE_TIME to updateTime,
            FirebaseConstants.Firestore.DUE_TIME to dueTime,
            FirebaseConstants.Firestore.SCHEDULED_TIME to scheduledTime,
            FirebaseConstants.Firestore.MAX_POINTS to maxPoints,
            FirebaseConstants.Firestore.WORK_TYPE to workType,
            FirebaseConstants.Firestore.CREATOR_USER_ID to creatorUserId
        )
    }
}