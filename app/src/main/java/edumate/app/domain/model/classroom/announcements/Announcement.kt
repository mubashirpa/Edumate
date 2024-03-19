package edumate.app.domain.model.classroom.announcements

import edumate.app.domain.model.classroom.Material
import edumate.app.domain.model.userProfiles.UserProfile

data class Announcement(
    val alternateLink: String? = null,
    val creationTime: String? = null,
    val creator: UserProfile? = null,
    val creatorUserId: String? = null,
    val id: String? = null,
    val materials: List<Material>? = null,
    val state: AnnouncementState? = null,
    val text: String? = null,
    val updateTime: String? = null,
)
