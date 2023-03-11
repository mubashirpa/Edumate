package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.CourseWorkType
import edumate.app.domain.model.Material
import java.util.Date

data class CourseWorkDto(
    val courseId: String = "",
    @ServerTimestamp val creationTime: Date? = null,
    val creatorUserId: String = "",
    val description: String? = "",
    val dueTime: Date? = null,
    val id: String = "",
    val materials: List<Material> = listOf(),
    val maxPoints: Int = 0,
    val scheduledTime: Date? = null,
    val title: String = "",
    @ServerTimestamp val updateTime: Date? = null,
    val workType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.COURSE_ID to courseId,
            FirebaseConstants.Firestore.CREATION_TIME to creationTime,
            FirebaseConstants.Firestore.CREATOR_USER_ID to creatorUserId,
            FirebaseConstants.Firestore.DESCRIPTION to description,
            FirebaseConstants.Firestore.DUE_TIME to dueTime,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.MATERIALS to materials,
            FirebaseConstants.Firestore.MAX_POINTS to maxPoints,
            FirebaseConstants.Firestore.SCHEDULED_TIME to scheduledTime,
            FirebaseConstants.Firestore.TITLE to title,
            FirebaseConstants.Firestore.UPDATE_TIME to updateTime,
            FirebaseConstants.Firestore.WORK_TYPE to workType
        )
    }
}