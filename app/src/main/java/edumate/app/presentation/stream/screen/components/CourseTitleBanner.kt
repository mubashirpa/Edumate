package edumate.app.presentation.stream.screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import edumate.app.core.ext.toHslColor
import edumate.app.domain.model.courses.Course

@Composable
fun CourseTitleBanner(course: Course) {
    val id = course.id
    val name = course.name
    val section = course.section
    val containerColor = remember(id, name, section) {
        val n = listOf(name, section)
            .joinToString(separator = "")
            .uppercase()
        Color("$id / $n".toHslColor())
    }

    Card(
        modifier = Modifier.aspectRatio(327f / 121f),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        ListItem(
            headlineContent = {
                Text(
                    text = name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            supportingContent = {
                if (section != null) {
                    Text(
                        text = section,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}