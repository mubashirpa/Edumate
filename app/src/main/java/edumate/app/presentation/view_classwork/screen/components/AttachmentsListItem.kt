package edumate.app.presentation.view_classwork.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.Material

@Composable
fun AttachmentsListItem(
    attachment: Material,
    onClick: (url: String?) -> Unit
) {
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
    val fileType = fileUtils.getFileType(attachment.driveFile?.type)
    val icon = when (fileType) {
        FileType.IMAGE -> Icons.Default.Image
        FileType.VIDEO -> Icons.Default.VideoFile
        FileType.AUDIO -> Icons.Default.AudioFile
        FileType.PDF -> Icons.Default.PictureAsPdf
        FileType.UNKNOWN -> Icons.Default.InsertDriveFile
    }
    val title: String = when {
        attachment.driveFile != null -> {
            attachment.driveFile.title ?: attachment.driveFile.url
        }

        attachment.link != null -> {
            attachment.link.title ?: attachment.link.url
        }

        else -> {
            ""
        }
    }
    val url = when {
        attachment.driveFile != null -> {
            attachment.driveFile.url
        }

        attachment.link != null -> {
            attachment.link.url
        }

        else -> {
            null
        }
    }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = url != null, onClick = { onClick(url) })
    ) {
        OutlinedCard(modifier = Modifier.aspectRatio(16f / 9f)) {
            when {
                attachment.driveFile != null -> {
                    when (fileType) {
                        FileType.IMAGE -> {
                            ImageThumbnail(
                                url = attachment.driveFile.url,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        FileType.VIDEO -> {
                            VideoThumbnail(
                                url = attachment.driveFile.url,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = icon, contentDescription = null)
                            }
                        }
                    }
                }

                attachment.link != null -> {
                    val thumbnail = attachment.link.thumbnailUrl
                    if (thumbnail.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null)
                        }
                    } else {
                        ImageThumbnail(
                            url = thumbnail,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.padding(horizontal = 4.dp)) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                modifier = Modifier.align(Alignment.CenterVertically),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun VideoThumbnail(
    url: String,
    modifier: Modifier = Modifier
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(VideoFrameDecoder.Factory())
        }.crossfade(true).build()
    val painter = rememberAsyncImagePainter(
        model = url,
        imageLoader = imageLoader
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ImageThumbnail(
    url: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}