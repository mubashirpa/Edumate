package edumate.app.presentation.teaching.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.R.plurals as Plurals
import edumate.app.R.string as Strings
import edumate.app.domain.model.courses.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachingListItem(
    course: Course,
    index: Int,
    modifier: Modifier = Modifier,
    onShareClick: (link: String) -> Unit,
    onEditClick: (courseId: String) -> Unit,
    onDeleteClick: (courseId: String) -> Unit,
    onClick: (courseId: String) -> Unit
) {
    val images = remember {
        listOf(
            "https://gstatic.com/classroom/themes/Geography_thumb.jpg",
            "https://gstatic.com/classroom/themes/Writing_thumb.jpg",
            "https://gstatic.com/classroom/themes/Math_thumb.jpg",
            "https://gstatic.com/classroom/themes/Chemistry_thumb.jpg",
            "https://gstatic.com/classroom/themes/Physics_thumb.jpg",
            "https://gstatic.com/classroom/themes/Psychology_thumb.jpg",
            "https://gstatic.com/classroom/themes/img_graduation_thumb.jpg",
            "https://gstatic.com/classroom/themes/SocialStudies_thumb.jpg"
        )
    }
    val backgroundImage = images[index % images.size]

    Card(
        onClick = { onClick(course.id) },
        modifier = modifier.aspectRatio(8f / 3f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(backgroundImage)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 8.dp)
                    .padding(vertical = 12.dp)
            ) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        // headlineContent
                        Text(
                            text = course.name,
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        // supportingContent
                        Text(
                            text = course.section.orEmpty(),
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    // trailingContent
                    TeachingMenuButton(
                        onShareClick = { onShareClick(course.alternateLink) },
                        onEditClick = { onEditClick(course.id) },
                        onDeleteClick = { onDeleteClick(course.id) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = pluralStringResource(
                        id = Plurals.number_of_students,
                        count = course.courseGroupId.size,
                        course.courseGroupId.size
                    ),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun TeachingMenuButton(
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.White
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
            DropdownMenuItem(
                text = { Text(stringResource(id = Strings.delete)) },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}