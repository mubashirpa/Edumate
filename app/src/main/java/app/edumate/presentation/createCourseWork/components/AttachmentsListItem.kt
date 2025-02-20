package app.edumate.presentation.createCourseWork.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.material.Material
import app.edumate.presentation.components.ImageThumbnail
import app.edumate.presentation.components.ThumbnailPlaceholder
import app.edumate.presentation.components.VideoThumbnail

@Composable
fun AttachmentsListItem(
    material: Material,
    fileUtils: FileUtils,
    onRemoveAttachmentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val driveFile = material.driveFile
    val link = material.link
    val thumbnailModifier =
        Modifier
            .size(24.dp)
            .clip(MaterialTheme.shapes.extraSmall)
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

    ListItem(
        headlineContent = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        modifier = modifier,
        leadingContent = {
            when {
                thumbnail != null -> {
                    ImageThumbnail(
                        context = context,
                        imageUrl = thumbnail,
                        imageSize = 24,
                        modifier = thumbnailModifier,
                        icon = icon,
                    )
                }

                mimeType == FileType.IMAGE -> {
                    ImageThumbnail(
                        context = context,
                        imageUrl = url,
                        imageSize = 24,
                        modifier = thumbnailModifier,
                    )
                }

                mimeType == FileType.VIDEO -> {
                    VideoThumbnail(
                        context = context,
                        videoUrl = url,
                        imageSize = 24,
                        modifier = thumbnailModifier,
                    )
                }

                else -> {
                    ThumbnailPlaceholder(
                        icon = icon,
                        modifier = thumbnailModifier,
                    )
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onRemoveAttachmentClick) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                )
            }
        },
    )
}
