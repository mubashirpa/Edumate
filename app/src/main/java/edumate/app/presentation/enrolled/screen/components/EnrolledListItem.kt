package edumate.app.presentation.enrolled.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.ext.toHslColor
import edumate.app.domain.model.courses.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrolledListItem(
    course: Course,
    onUnEnrollClick: (courseId: String) -> Unit,
    onClick: (courseId: String) -> Unit
) {
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
        onClick = { onClick(id) },
        modifier = Modifier.aspectRatio(8f / 3f),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
                .padding(vertical = 16.dp)
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = section.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                EnrolledMenuButton(
                    onUnEnrollClick = {
                        onUnEnrollClick(course.id)
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // TODO("Use owner user name")
            Text(
                text = course.ownerId,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun EnrolledMenuButton(
    onUnEnrollClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = Strings.unenrol)) },
                onClick = {
                    expanded = false
                    onUnEnrollClick()
                }
            )
        }
    }
}