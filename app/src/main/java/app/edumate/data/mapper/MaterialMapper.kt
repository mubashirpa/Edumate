package app.edumate.data.mapper

import app.edumate.data.remote.dto.material.DriveFileDto
import app.edumate.data.remote.dto.material.LinkDto
import app.edumate.data.remote.dto.material.MaterialDto
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Link
import app.edumate.domain.model.material.Material

fun MaterialDto.toMaterialDomainModel(): Material =
    Material(
        driveFile = driveFile?.toDriveFileDomainModel(),
        link = link?.toLinkDomainModel(),
    )

fun DriveFileDto.toDriveFileDomainModel(): DriveFile =
    DriveFile(
        alternateLink = alternateLink,
        id = id,
        thumbnailUrl = thumbnailUrl,
        title = title,
    )

fun LinkDto.toLinkDomainModel(): Link =
    Link(
        thumbnailUrl = thumbnailUrl,
        title = title,
        url = url,
    )

fun Material.toMaterialDto(): MaterialDto =
    MaterialDto(
        driveFile = driveFile?.toDriveFileDto(),
        link = link?.toLinkDto(),
    )

fun DriveFile.toDriveFileDto(): DriveFileDto =
    DriveFileDto(
        alternateLink = alternateLink,
        id = id,
        thumbnailUrl = thumbnailUrl,
        title = title,
    )

fun Link.toLinkDto(): LinkDto =
    LinkDto(
        thumbnailUrl = thumbnailUrl,
        title = title,
        url = url,
    )
