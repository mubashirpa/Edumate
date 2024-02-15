package edumate.app.data.mapper

import edumate.app.core.utils.enumValueOf
import edumate.app.data.remote.dto.classroom.announcements.Announcement
import edumate.app.domain.model.classroom.announcements.Announcement as AnnouncementDomainModel

fun Announcement.toAnnouncementDomainModel(): AnnouncementDomainModel {
    return AnnouncementDomainModel(
        creationTime = creationTime,
        creatorUserId = creatorUserId,
        id = id,
        materials = materials?.map { it.toMaterialDomainModel() },
        state = enumValueOf(state?.name),
        text = text,
        updateTime = updateTime,
    )
}

fun AnnouncementDomainModel.toAnnouncement(): Announcement {
    return Announcement(
        creationTime = creationTime,
        creatorUserId = creatorUserId,
        id = id,
        materials = materials?.map { it.toMaterial() },
        text = text,
        updateTime = updateTime,
    )
}
