package app.edumate.presentation.teaching.components

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
fun TeachingListItem(
    onClick: (id: String) -> Unit,
    teachingCourse: Course,
    isOwner: Boolean,
    onShareClick: (link: String) -> Unit,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = { teachingCourse.id?.let(onClick) },
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
                        text = teachingCourse.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = teachingCourse.section.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                TeachingMenuButton(
                    isOwner = isOwner,
                    onShareClick = {
                        onShareClick(teachingCourse.joinLink)
                    },
                    onEditClick = {
                        teachingCourse.id?.let(onEditClick)
                    },
                    onDeleteClick = onDeleteClick,
                    onLeaveClick = onLeaveClick,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            InputChip(
                selected = false,
                onClick = {},
                label = {
                    Text(text = teachingCourse.owner?.name.orEmpty())
                },
                modifier = Modifier.align(Alignment.End),
                avatar = {
                    UserAvatar(
                        id = teachingCourse.owner?.id.orEmpty(),
                        fullName = teachingCourse.owner?.name.orEmpty(),
                        photoUrl = teachingCourse.owner?.photoUrl,
                        size = InputChipDefaults.AvatarSize,
                        shape = MaterialTheme.shapes.medium,
                        textStyle = MaterialTheme.typography.labelSmall,
                    )
                },
            )
        }
    }
}

@Composable
private fun TeachingMenuButton(
    isOwner: Boolean,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLeaveClick: () -> Unit,
) {
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
                    Text(stringResource(id = R.string.share_invite_link))
                },
                onClick = {
                    expanded = false
                    onShareClick()
                },
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(id = R.string.edit))
                },
                onClick = {
                    expanded = false
                    onEditClick()
                },
            )
            if (isOwner) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(id = R.string.delete))
                    },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                    },
                )
            } else {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(id = R.string.leave))
                    },
                    onClick = {
                        expanded = false
                        onLeaveClick()
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EnrolledListItemPreview() {
    EdumateTheme {
        TeachingListItem(
            onClick = {},
            teachingCourse =
                Course(
                    name = "Mathematics",
                    owner = User(name = "John Doe"),
                    section = "Period 1",
                ),
            isOwner = true,
            onShareClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onLeaveClick = {},
        )
    }
}
