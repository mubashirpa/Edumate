package app.edumate.presentation.stream.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Link
import app.edumate.domain.model.material.Material
import app.edumate.presentation.components.ImageThumbnail
import app.edumate.presentation.components.ThumbnailPlaceholder
import app.edumate.presentation.components.VideoThumbnail

@Composable
fun AttachmentsListItem(
    material: Material,
    fileUtils: FileUtils,
    onClickFile: (mimeType: FileType, url: String, title: String?) -> Unit,
    onClickLink: (url: String) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AttachmentsListItemContent(
        driveFile = material.driveFile,
        link = material.link,
        fileUtils = fileUtils,
        onClickLink = onClickLink,
        onClickFile = onClickFile,
        modifier = modifier,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier.clickable(onClick = onRemoveClick),
            )
        },
    )
}

@Composable
fun AttachmentsListItem(
    material: Material,
    fileUtils: FileUtils,
    onClickFile: (mimeType: FileType, url: String, title: String?) -> Unit,
    onClickLink: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AttachmentsListItemContent(
        driveFile = material.driveFile,
        link = material.link,
        fileUtils = fileUtils,
        onClickFile = onClickFile,
        onClickLink = onClickLink,
        modifier = modifier,
    )
}

@Composable
private fun AttachmentsListItemContent(
    driveFile: DriveFile?,
    link: Link?,
    fileUtils: FileUtils,
    onClickFile: (mimeType: FileType, url: String, title: String?) -> Unit,
    onClickLink: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val thumbnailModifier =
        Modifier
            .size(InputChipDefaults.AvatarSize)
            .clip(CircleShape)
    val imageSize = with(density) { InputChipDefaults.AvatarSize.toPx().toInt() }
    val iconSize = 18
    val mimeType = fileUtils.getFileTypeFromMimeType(driveFile?.mimeType)
    val title: String
    val icon: ImageVector
    val thumbnail: String?
    val url: String?

    when {
        driveFile != null -> {
            title = driveFile.title.orEmpty()
            icon =
                when (mimeType) {
                    FileType.IMAGE -> Icons.Default.Image
                    FileType.VIDEO -> Icons.Default.VideoFile
                    FileType.AUDIO -> Icons.Default.AudioFile
                    FileType.PDF -> Icons.Default.PictureAsPdf
                    FileType.UNKNOWN -> Icons.AutoMirrored.Filled.InsertDriveFile
                }
            thumbnail = driveFile.thumbnailUrl
            url = driveFile.alternateLink
        }

        link != null -> {
            title = link.title.orEmpty()
            icon = Icons.Default.Link
            thumbnail = link.thumbnailUrl
            url = link.url
        }

        else -> return
    }

    InputChip(
        selected = false,
        onClick = {
            url?.let {
                when {
                    link != null -> onClickLink(url)
                    driveFile != null -> onClickFile(mimeType, url, driveFile.title)
                }
            }
        },
        label = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        modifier = modifier,
        avatar = {
            when {
                thumbnail != null -> {
                    ImageThumbnail(
                        context = context,
                        imageUrl = thumbnail,
                        imageSize = imageSize,
                        modifier = thumbnailModifier,
                        iconSize = iconSize,
                        icon = icon,
                    )
                }

                mimeType == FileType.IMAGE -> {
                    ImageThumbnail(
                        context = context,
                        imageUrl = url,
                        imageSize = imageSize,
                        modifier = thumbnailModifier,
                        iconSize = iconSize,
                    )
                }

                mimeType == FileType.VIDEO -> {
                    VideoThumbnail(
                        context = context,
                        videoUrl = url,
                        imageSize = imageSize,
                        modifier = thumbnailModifier,
                        iconSize = iconSize,
                    )
                }

                else -> {
                    ThumbnailPlaceholder(
                        icon = icon,
                        modifier = thumbnailModifier,
                        iconSize = iconSize,
                    )
                }
            }
        },
        trailingIcon = trailingIcon,
    )
}
