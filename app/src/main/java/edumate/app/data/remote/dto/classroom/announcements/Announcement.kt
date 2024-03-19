package edumate.app.data.remote.dto.classroom.announcements

import edumate.app.data.remote.dto.classroom.AssigneeMode
import edumate.app.data.remote.dto.classroom.IndividualStudentsOptions
import edumate.app.data.remote.dto.classroom.Material
import edumate.app.data.remote.dto.userProfiles.UserProfile
import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val alternateLink: String? = null,
    val assigneeMode: AssigneeMode? = null,
    val courseId: String? = null,
    val creationTime: String? = null,
    val creator: UserProfile? = null,
    val creatorUserId: String? = null,
    val id: String? = null,
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val materials: List<Material>? = null,
    val state: AnnouncementState? = null,
    val text: String? = null,
    val updateTime: String? = null,
)
