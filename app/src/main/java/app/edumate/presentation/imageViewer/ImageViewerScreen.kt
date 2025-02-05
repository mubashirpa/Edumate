package app.edumate.presentation.imageViewer

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageViewerScreen(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var aspectRatio by remember { mutableFloatStateOf(1f) }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val maxWidth = constraints.maxWidth.toFloat()
        val maxHeight = constraints.maxHeight.toFloat()

        SubcomposeAsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
        ) {
            val state by painter.state.collectAsState()
            when (state) {
                AsyncImagePainter.State.Empty -> {}

                is AsyncImagePainter.State.Error -> {}

                is AsyncImagePainter.State.Loading -> {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    val width = state.painter?.intrinsicSize?.width ?: maxWidth
                    val height = state.painter?.intrinsicSize?.height ?: maxHeight
                    aspectRatio = width / height

                    val state =
                        rememberTransformableState { zoomChange, panChange, rotationChange ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)

                            val extraWidth = (scale - 1) * width
                            val extraHeight = (scale - 1) * height
                            val maxX = extraWidth / 2
                            val maxY = extraHeight / 2

                            offset =
                                Offset(
                                    x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                                    y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY),
                                )
                        }

                    SubcomposeAsyncImageContent(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = offset.x
                                    translationY = offset.y
                                }.transformable(state),
                    )
                }
            }
        }
    }
}
