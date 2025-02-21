package app.edumate.presentation.enrolled.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.course.Course
import app.edumate.domain.model.user.User
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.theme.EdumateTheme

@Composable
fun EnrolledListItem(
    onClick: (id: String) -> Unit,
    enrolledCourse: Course,
    onCourseUnenroll: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = { enrolledCourse.id?.let(onClick) },
        modifier = modifier.aspectRatio(8f / 3f),
    ) {
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
                        text = enrolledCourse.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = enrolledCourse.section.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                EnrolledMenuButton(
                    onCourseUnenroll = {
                        enrolledCourse.id?.let(onCourseUnenroll)
                    },
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            InputChip(
                selected = false,
                onClick = {},
                label = {
                    Text(text = enrolledCourse.owner?.name.orEmpty())
                },
                modifier = Modifier.align(Alignment.End),
                avatar = {
                    UserAvatar(
                        id = enrolledCourse.owner?.id.orEmpty(),
                        fullName = enrolledCourse.owner?.name.orEmpty(),
                        photoUrl = enrolledCourse.owner?.photoUrl,
                        size = InputChipDefaults.AvatarSize.value.toInt(),
                        shape = MaterialTheme.shapes.medium,
                        textStyle = MaterialTheme.typography.labelSmall,
                    )
                },
            )
        }
    }
}

@Composable
private fun EnrolledMenuButton(onCourseUnenroll: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(id = R.string.unenroll))
                },
                onClick = {
                    expanded = false
                    onCourseUnenroll()
                },
            )
        }
    }
}

@Preview
@Composable
private fun EnrolledListItemPreview() {
    EdumateTheme {
        EnrolledListItem(
            onClick = {},
            enrolledCourse =
                Course(
                    name = "Mathematics",
                    owner = User(name = "John Doe"),
                    section = "Period 1",
                ),
            onCourseUnenroll = {},
        )
    }
}
