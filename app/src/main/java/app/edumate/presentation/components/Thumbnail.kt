package app.edumate.presentation.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder

@Composable
fun ImageThumbnail(
    context: Context,
    imageUrl: String?,
    imageSize: Int,
    modifier: Modifier = Modifier,
    iconSize: Int = 24,
) {
    val placeholder: @Composable () -> Unit = {
        ThumbnailPlaceholder(
            icon = Icons.Default.Image,
            modifier = Modifier.fillMaxSize(),
            iconSize = iconSize,
        )
    }

    SubcomposeAsyncImage(
        model =
            ImageRequest
                .Builder(context)
                .data(imageUrl)
                .size(imageSize)
                .build(),
        contentDescription = null,
        modifier = modifier,
        loading = {
            placeholder()
        },
        error = {
            placeholder()
        },
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun VideoThumbnail(
    context: Context,
    videoUrl: String?,
    imageSize: Int,
    modifier: Modifier = Modifier,
    iconSize: Int = 24,
) {
    val placeholder: @Composable () -> Unit = {
        ThumbnailPlaceholder(
            icon = Icons.Default.VideoFile,
            modifier = Modifier.fillMaxSize(),
            iconSize = iconSize,
        )
    }
    val request =
        ImageRequest
            .Builder(context)
            .data(videoUrl)
            .size(imageSize)
            .build()
    val videoEnabledLoader =
        ImageLoader
            .Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }.build()

    SubcomposeAsyncImage(
        model = request,
        contentDescription = null,
        imageLoader = videoEnabledLoader,
        modifier = modifier,
        loading = {
            placeholder()
        },
        error = {
            placeholder()
        },
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ThumbnailPlaceholder(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconSize: Int = 24,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize.dp),
        )
    }
}
