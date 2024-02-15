package edumate.app.domain.model.classroom.announcements

import edumate.app.domain.model.classroom.Material

data class Announcement(
    val creationTime: String? = null,
    val creatorUserId: String? = null,
    val id: String? = null,
    val materials: List<Material>? = null,
    val state: AnnouncementState? = null,
    val text: String? = null,
    val updateTime: String? = null,
)
