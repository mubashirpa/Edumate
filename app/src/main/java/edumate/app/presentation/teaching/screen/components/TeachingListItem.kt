package edumate.app.presentation.teaching.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.plurals as Plurals
import edumate.app.R.string as Strings
import edumate.app.domain.model.courses.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachingListItem(
    course: Course,
    onShareClick: (link: String) -> Unit,
    onEditClick: (courseId: String) -> Unit,
    onClick: (courseId: String) -> Unit
) {
    Card(
        onClick = {
            onClick(course.id)
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
                        onShareClick(course.alternateLink)
                    },
                    onEditClick = {
                        onEditClick(course.id)
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = pluralStringResource(
                    id = Plurals.number_of_students,
                    count = course.courseGroupId.size,
                    course.courseGroupId.size
                ),
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