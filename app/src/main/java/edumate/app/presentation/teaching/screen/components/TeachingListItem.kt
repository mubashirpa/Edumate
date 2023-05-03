package edumate.app.presentation.teaching.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        modifier = Modifier.aspectRatio(327f / 121f)
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

@Composable
fun TeachingListItemBeta(
    course: Course,
    index: Int,
    onShareClick: (link: String) -> Unit,
    onEditClick: (courseId: String) -> Unit,
    onClick: (courseId: String) -> Unit
) {
    val gradient = when (index % 2) {
        0 -> listOf(Color(0xff57eff5), Color(0xffc8bbfd))
        else -> listOf(Color(0xffc186fa), Color(0xfff985aa))
    }

    Box(
        modifier = Modifier
            .aspectRatio(327f / 121f)
            .clip(MaterialTheme.shapes.medium)
            .background(Brush.horizontalGradient(colors = gradient))
            .clickable { onClick(course.id) }
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(vertical = 16.dp)
                .fillMaxSize()
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.name,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = course.section.orEmpty(),
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                TeachingMenuButtonBeta(
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
                color = Color.Black,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun TeachingMenuButtonBeta(
    onShareClick: () -> Unit,
    onEditClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.Black
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