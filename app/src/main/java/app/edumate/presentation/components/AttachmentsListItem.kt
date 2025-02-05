package app.edumate.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Link
import app.edumate.domain.model.material.Material
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun AttachmentsListItem(
    material: Material,
    fileUtils: FileUtils,
    onClickFile: (mimeType: FileType, url: String, title: String?) -> Unit,
    onClickLink: (url: String) -> Unit,
) {
    AttachmentsListItemContent(
        driveFile = material.driveFile,
        link = material.link,
        fileUtils = fileUtils,
        onClickFile = onClickFile,
        onClickLink = onClickLink,
    )
}

@Composable
private fun AttachmentsListItemContent(
    driveFile: DriveFile?,
    link: Link?,
    fileUtils: FileUtils,
    onClickFile: (mimeType: FileType, url: String, title: String?) -> Unit,
    onClickLink: (url: String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
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

        else -> {
            title = ""
            icon = Icons.Default.Attachment
            thumbnail = null
            url = null
        }
    }

    Column(
        modifier =
            Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = url != null,
                    onClick = {
                        when {
                            link != null -> onClickLink(url!!)
                            driveFile != null -> onClickFile(mimeType, url!!, driveFile.title)
                        }
                    },
                ),
    ) {
        OutlinedCard(
            modifier =
                Modifier
                    .aspectRatio(16f / 9f)
                    .indication(interactionSource, ripple()),
        ) {
            if (thumbnail != null) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(thumbnail)
                            .crossfade(true)
                            .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.padding(horizontal = 4.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                modifier = Modifier.align(Alignment.CenterVertically),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}
