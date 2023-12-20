package edumate.app.data.remote.dto

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.IndividualStudentsOptions
import edumate.app.domain.model.Material
import edumate.app.domain.model.announcements.AnnouncementState
import edumate.app.domain.model.user_profiles.UserProfile
import java.util.Date

@IgnoreExtraProperties
data class AnnouncementDto(
    val courseId: String = "",
    val id: String = "",
    val text: String = "",
    val materials: List<Material> = emptyList(),
    val state: AnnouncementState = AnnouncementState.ANNOUNCEMENT_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    val creationTime: Long? = null,
    val updateTime: Long? = null,
    val scheduledTime: Long? = null,
    val assigneeMode: AssigneeMode = AssigneeMode.ASSIGNEE_MODE_UNSPECIFIED,
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val creatorUserId: String = "",
    val creatorProfile: UserProfile? = null,
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        val clientTime = Date().time
        val map: MutableMap<String, Any?> =
            mutableMapOf(
                FirebaseConstants.Database.COURSE_ID to courseId,
                FirebaseConstants.Database.ID to id,
                FirebaseConstants.Database.TEXT to text,
                FirebaseConstants.Database.MATERIALS to materials,
                FirebaseConstants.Database.STATE to state,
                FirebaseConstants.Database.ALTERNATE_LINK to alternateLink,
                FirebaseConstants.Database.CREATION_TIME to (creationTime ?: clientTime),
                FirebaseConstants.Database.UPDATE_TIME to clientTime,
                FirebaseConstants.Database.SCHEDULED_TIME to scheduledTime,
                FirebaseConstants.Database.ASSIGNEE_MODE to assigneeMode,
                FirebaseConstants.Database.CREATOR_USER_ID to creatorUserId,
                FirebaseConstants.Database.CREATOR_PROFILE to creatorProfile,
            )
        if (assigneeMode == AssigneeMode.INDIVIDUAL_STUDENTS) {
            map[FirebaseConstants.Database.INDIVIDUAL_STUDENTS_OPTIONS] = individualStudentsOptions
        }
        return map
    }
}
