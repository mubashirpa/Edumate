package app.edumate.presentation.components

import androidx.annotation.ColorInt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.core.graphics.ColorUtils
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlin.math.absoluteValue

@Composable
fun UserAvatar(
    id: String,
    fullName: String,
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    shape: Shape = CircleShape,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val name = fullName.split(" ")
    val firstName = name[0]
    val lastName = if (name.size > 1) name[1] else ""
    val avatar: @Composable () -> Unit = {
        TextAvatar(
            id = id,
            firstName = firstName,
            lastName = lastName,
            modifier = modifier,
            size = size,
            shape = shape,
            textStyle = textStyle,
        )
    }

    if (photoUrl != null) {
        SubcomposeAsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(photoUrl)
                    .size(size.value.toInt())
                    .crossfade(true)
                    .build(),
            contentDescription = null,
            modifier =
                modifier
                    .size(size)
                    .clip(shape),
            loading = {
                avatar()
            },
            error = {
                avatar()
            },
            contentScale = ContentScale.Crop,
        )
    } else {
        avatar()
    }
}

@Composable
private fun TextAvatar(
    id: String,
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    shape: Shape = CircleShape,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val color =
        remember(id, firstName, lastName) {
            val name =
                listOf(firstName, lastName)
                    .joinToString(separator = "")
                    .uppercase()
            Color("$id / $name".toHslColor())
        }
    val initials = (firstName.take(1) + lastName.take(1)).uppercase()

    Box(
        modifier =
            modifier
                .size(size)
                .clip(shape)
                .background(color),
        contentAlignment = Alignment.Center,
    ) {
        AutoResizedText(
            text = initials,
            style = textStyle,
            color = Color.White,
        )
    }
}

@Composable
private fun AutoResizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    var resizedTextStyle by remember { mutableStateOf(style) }
    var shouldDraw by remember { mutableStateOf(false) }
    val defaultFontSize = MaterialTheme.typography.bodyMedium.fontSize

    Text(
        text = text,
        color = color,
        modifier =
            modifier.drawWithContent {
                if (shouldDraw) {
                    drawContent()
                }
            },
        softWrap = false,
        style = resizedTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (style.fontSize.isUnspecified) {
                    resizedTextStyle =
                        resizedTextStyle.copy(
                            fontSize = defaultFontSize,
                        )
                }
                resizedTextStyle =
                    resizedTextStyle.copy(
                        fontSize = resizedTextStyle.fontSize * 0.95,
                    )
            } else {
                shouldDraw = true
            }
        },
    )
}

@ColorInt
private fun String.toHslColor(
    saturation: Float = 0.5f,
    lightness: Float = 0.4f,
): Int {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return ColorUtils.HSLToColor(floatArrayOf(hue.absoluteValue.toFloat(), saturation, lightness))
}
