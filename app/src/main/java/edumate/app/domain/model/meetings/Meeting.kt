package edumate.app.domain.model.meetings

import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.IndividualStudentsOptions

data class Meeting(
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
    val updateTime: Long? = null
)