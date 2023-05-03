package edumate.app.domain.model.announcements

import java.util.Date

data class Announcement(
    val courseId: String = "",
    val id: String = "",
    val text: String = "",
    val materials: List<Material> = emptyList(),
    val state: AnnouncementState = AnnouncementState.ANNOUNCEMENT_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    val creationTime: Date? = null,
    val updateTime: Date? = null,
    val scheduledTime: Date? = null,
    val assigneeMode: AssigneeMode = AssigneeMode.ASSIGNEE_MODE_UNSPECIFIED,
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val creatorUserId: String = ""
)