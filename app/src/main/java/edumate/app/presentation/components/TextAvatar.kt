package edumate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edumate.app.core.ext.toHslColor

@Composable
fun TextAvatar(
    id: String,
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
    val color = remember(id, firstName, lastName) {
        val name = listOf(firstName, lastName)
            .joinToString(separator = "")
            .uppercase()
        Color("$id / $name".toHslColor())
    }
    val initials = (firstName.take(1) + lastName.take(1)).uppercase()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        AutoResizedText(
            text = initials,
            style = textStyle,
            color = Color.White
        )
    }
}