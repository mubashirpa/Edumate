package app.edumate.presentation.imageViewer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import app.edumate.R
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageViewerScreen(
    imageUrl: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var aspectRatio by remember { mutableFloatStateOf(1f) }
    var topBarVisible by remember { mutableStateOf(true) }
    var expandedAppBarDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .clickable(interactionSource = null, indication = null) {
                        topBarVisible = !topBarVisible
                    },
        ) {
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()

            SubcomposeAsyncImage(
                model =
                    ImageRequest
                        .Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .align(Alignment.Center),
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
            AnimatedVisibility(
                visible = topBarVisible,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                TopAppBar(
                    title = {
                        title?.let {
                            Text(
                                text = it,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                            IconButton(
                                onClick = {
                                    expandedAppBarDropdown = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null,
                                )
                            }
                            DropdownMenu(
                                expanded = expandedAppBarDropdown,
                                onDismissRequest = {
                                    expandedAppBarDropdown = false
                                },
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(stringResource(id = R.string.open_with))
                                    },
                                    onClick = {
                                        expandedAppBarDropdown = false
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl))
                                        context.startActivity(intent)
                                    },
                                )
                            }
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        ),
                )
            }
        }
    }
}
