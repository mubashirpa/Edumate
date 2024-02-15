package edumate.app.data.remote.dto.classroom.announcements

import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementsDto(
    val announcements: List<Announcement>? = null,
    val nextPageToken: String? = null,
)
