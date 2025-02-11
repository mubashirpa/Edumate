package app.edumate.data.mapper

import app.edumate.data.remote.dto.announcement.AnnouncementDto
import app.edumate.domain.model.announcement.Announcement

fun AnnouncementDto.toAnnouncementDomainModel(): Announcement =
    Announcement(
        alternateLink = alternateLink,
        courseId = courseId,
        creationTime = creationTime,
        creator = creator?.toUserDomainModel(),
        creatorUserId = creatorUserId,
        id = id,
        materials = materials?.map { it.toMaterialDomainModel() },
        text = text,
        totalComments = totalComments,
        updateTime = updateTime,
    )

fun Announcement.toAnnouncementDto(): AnnouncementDto =
    AnnouncementDto(
        alternateLink = alternateLink,
        courseId = courseId,
        creationTime = creationTime,
        creator = creator?.toUserDto(),
        creatorUserId = creatorUserId,
        id = id,
        materials = materials?.map { it.toMaterialDto() },
        text = text,
        totalComments = totalComments,
        updateTime = updateTime,
    )
