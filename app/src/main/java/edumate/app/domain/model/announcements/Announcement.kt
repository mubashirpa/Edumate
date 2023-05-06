package edumate.app.domain.model.announcements

import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.IndividualStudentsOptions
import edumate.app.domain.model.Material
import edumate.app.domain.model.user_profiles.UserProfile

data class Announcement(
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
    val creatorProfile: UserProfile? = null
)