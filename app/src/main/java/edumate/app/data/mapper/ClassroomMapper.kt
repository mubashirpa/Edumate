package edumate.app.data.mapper

import edumate.app.data.remote.dto.classroom.DriveFile
import edumate.app.data.remote.dto.classroom.DriveFolder
import edumate.app.data.remote.dto.classroom.Link
import edumate.app.data.remote.dto.classroom.Material
import edumate.app.domain.model.classroom.DriveFile as DriveFileDomainModel
import edumate.app.domain.model.classroom.DriveFolder as DriveFolderDomainModel
import edumate.app.domain.model.classroom.Link as LinkDomainModel
import edumate.app.domain.model.classroom.Material as MaterialDomainModel

fun Material.toMaterialDomainModel(): MaterialDomainModel {
    return MaterialDomainModel(
        driveFile = driveFile?.toDriveFileDomainModel(),
        link = link?.toLinkDomainModel(),
    )
}

fun MaterialDomainModel.toMaterial(): Material {
    return Material(
        driveFile = driveFile?.toDriveFile(),
        link = link?.toLink(),
    )
}

fun DriveFolder.toDriveFolderDomainModel(): DriveFolderDomainModel {
    return DriveFolderDomainModel(
        alternateLink = alternateLink,
        id = id,
        title = title,
    )
}

fun DriveFolderDomainModel.toDriveFolder(): DriveFolder {
    return DriveFolder(
        alternateLink = alternateLink,
        id = id,
        title = title,
    )
}

fun Link.toLinkDomainModel(): LinkDomainModel {
    return LinkDomainModel(
        thumbnailUrl = thumbnailUrl,
        title = title,
        url = url,
    )
}

fun LinkDomainModel.toLink(): Link {
    return Link(
        thumbnailUrl = thumbnailUrl,
        title = title,
        url = url,
    )
}

private fun DriveFile.toDriveFileDomainModel(): DriveFileDomainModel {
    return DriveFileDomainModel(
        alternateLink = alternateLink,
        thumbnailUrl = thumbnailUrl,
        title = title,
    )
}

private fun DriveFileDomainModel.toDriveFile(): DriveFile {
    return DriveFile(
        alternateLink = alternateLink,
        thumbnailUrl = thumbnailUrl,
        title = title,
    )
}
