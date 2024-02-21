package edumate.app.presentation.enrolled.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.R.string as Strings

@Composable
fun EnrolledListItem(
    course: Course,
    modifier: Modifier = Modifier,
    onUnEnrollClick: (courseId: String) -> Unit,
    onClick: (courseId: String) -> Unit,
) {
    Card(
        onClick = {
            course.id?.let(onClick)
        },
        modifier = modifier.aspectRatio(8f / 3f),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(course.photoUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 8.dp)
                        .padding(vertical = 12.dp),
            ) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = course.name.orEmpty(),
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = course.section.orEmpty(),
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    EnrolledMenuButton(
                        onUnEnrollClick = {
                            course.id?.let(onUnEnrollClick)
                        },
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = course.owner?.name?.fullName.orEmpty(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun EnrolledMenuButton(onUnEnrollClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.White,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(id = Strings.unenrol))
                },
                onClick = {
                    expanded = false
                    onUnEnrollClick()
                },
            )
        }
    }
}
