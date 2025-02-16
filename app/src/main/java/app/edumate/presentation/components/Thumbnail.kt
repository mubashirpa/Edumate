package app.edumate.presentation.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder

@Composable
fun ImageThumbnail(
    context: Context,
    imageUrl: String?,
    imageSize: Int,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(context)
                .data(imageUrl)
                .size(imageSize)
                .crossfade(true)
                .build(),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun VideoThumbnail(
    context: Context,
    videoUrl: String?,
    imageSize: Int,
    modifier: Modifier = Modifier,
) {
    val videoEnabledLoader =
        ImageLoader
            .Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }.build()

    val request =
        ImageRequest
            .Builder(context)
            .data(videoUrl)
            .size(imageSize)
            .build()

    AsyncImage(
        model = request,
        imageLoader = videoEnabledLoader,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}
