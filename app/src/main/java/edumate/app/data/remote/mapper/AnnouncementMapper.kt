package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.AnnouncementDto
import edumate.app.domain.model.announcements.Announcement

fun AnnouncementDto.toAnnouncement(): Announcement {
    return Announcement(
        courseId,
        id,
        text,
        materials,
        state,
        alternateLink,
        creationTime,
        updateTime,
        scheduledTime,
        assigneeMode,
        individualStudentsOptions,
        creatorUserId
    )
}

fun Announcement.toAnnouncementDto(): AnnouncementDto {
    return AnnouncementDto(
        courseId,
        id,
        text,
        materials,
        state,
        alternateLink,
        creationTime,
        updateTime,
        scheduledTime,
        assigneeMode,
        individualStudentsOptions,
        creatorUserId
    )
}