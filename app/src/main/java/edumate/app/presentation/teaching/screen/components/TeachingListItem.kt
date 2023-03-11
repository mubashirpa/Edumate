package edumate.app.presentation.teaching.screen.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.domain.model.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachingListItem(
    course: Course,
    onShareClick: (link: String) -> Unit,
    onEditClick: (courseId: String) -> Unit,
    onClick: (name: String, courseId: String) -> Unit
) {
    val context = LocalContext.current
    Card(
        onClick = {
            onClick(course.name, course.id.orEmpty())
        },
        modifier = Modifier.aspectRatio(21f / 9f)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                bottom = 16.dp
            )
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = course.section.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                TeachingMenuButton(
                    onShareClick = {
                        onShareClick(course.alternateLink.orEmpty())
                    },
                    onEditClick = {
                        onEditClick(course.id.orEmpty())
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = studentSize(context, course.students?.size ?: 0),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun TeachingMenuButton(
    onShareClick: () -> Unit,
    onEditClick: () -> Unit
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
                text = { Text(stringResource(id = Strings.share_invitation_link)) },
                onClick = {
                    expanded = false
                    onShareClick()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = Strings.edit)) },
                onClick = {
                    expanded = false
                    onEditClick()
                }
            )
        }
    }
}

private fun studentSize(context: Context, size: Int): String {
    return if (size == 1) {
        "$size ${context.getString(Strings.student).lowercase()}"
    } else {
        "$size ${context.getString(Strings.students).lowercase()}"
    }
}