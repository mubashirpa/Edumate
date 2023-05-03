package edumate.app.data.remote.dto

import com.google.firebase.firestore.ServerTimestamp
import edumate.app.domain.model.announcements.AnnouncementState
import edumate.app.domain.model.announcements.AssigneeMode
import edumate.app.domain.model.announcements.IndividualStudentsOptions
import edumate.app.domain.model.announcements.Material
import java.util.Date

data class AnnouncementDto(
    val courseId: String = "",
    val id: String = "",
    val text: String = "",
    val materials: List<Material> = emptyList(),
    val state: AnnouncementState = AnnouncementState.ANNOUNCEMENT_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    @ServerTimestamp
    val creationTime: Date? = null,
    @ServerTimestamp
    val updateTime: Date? = null,
    val scheduledTime: Date? = null,
    val assigneeMode: AssigneeMode = AssigneeMode.ASSIGNEE_MODE_UNSPECIFIED,
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val creatorUserId: String = ""
)