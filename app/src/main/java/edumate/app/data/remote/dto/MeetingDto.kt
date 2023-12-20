package edumate.app.data.remote.dto

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.IndividualStudentsOptions
import edumate.app.domain.model.meetings.MeetingState
import java.util.Date

@IgnoreExtraProperties
data class MeetingDto(
    val alternateLink: String = "",
    val assigneeMode: AssigneeMode = AssigneeMode.ASSIGNEE_MODE_UNSPECIFIED,
    val courseId: String = "",
    val creationTime: Long? = null,
    val creatorUserId: String? = null,
    val id: String = "",
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val meetingId: String = "",
    val state: MeetingState = MeetingState.MEETING_STATE_UNSPECIFIED,
    val title: String? = null,
    val updateTime: Long? = null,
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        val clientTime = Date().time
        val map: MutableMap<String, Any?> =
            mutableMapOf(
                FirebaseConstants.Database.ALTERNATE_LINK to alternateLink,
                FirebaseConstants.Database.ASSIGNEE_MODE to assigneeMode,
                FirebaseConstants.Database.COURSE_ID to courseId,
                FirebaseConstants.Database.CREATION_TIME to (creationTime ?: clientTime),
                FirebaseConstants.Database.CREATOR_USER_ID to creatorUserId,
                FirebaseConstants.Database.ID to id,
                FirebaseConstants.Database.MEETING_ID to meetingId,
                FirebaseConstants.Database.STATE to state,
                FirebaseConstants.Database.TITLE to title,
                FirebaseConstants.Database.UPDATE_TIME to clientTime,
            )
        if (assigneeMode == AssigneeMode.INDIVIDUAL_STUDENTS) {
            map[FirebaseConstants.Database.INDIVIDUAL_STUDENTS_OPTIONS] = individualStudentsOptions
        }
        return map
    }
}
