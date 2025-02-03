package app.edumate.presentation.stream.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.utils.DateTimeUtils
import app.edumate.core.utils.RelativeDate
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.model.member.UserRole
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.courseDetails.CourseUserRole
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Composable
fun ReplyListItem(
    comment: Comment,
    itemUserRole: UserRole,
    currentUserRole: CourseUserRole,
    currentUserId: String,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val id = comment.id
    val creator = comment.creator
    val isCurrentUserCreator = comment.creatorUserId == currentUserId
    val creationDateTime =
        remember {
            try {
                Instant
                    .parse(comment.creationTime.orEmpty())
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            } catch (_: Exception) {
                null
            }
        }

    Column(modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(
                    text = creator?.name.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            supportingContent =
                creationDateTime?.let { creationTime ->
                    {
                        val time = formatDate(dateTime = creationTime)
                        Text(
                            text = time,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                },
            leadingContent = {
                UserAvatar(
                    id = creator?.id.orEmpty(),
                    fullName = creator?.name.orEmpty(),
                    photoUrl = creator?.avatarUrl,
                )
            },
            trailingContent = {
                MenuButton(
                    itemUserRole = itemUserRole,
                    currentUserRole = currentUserRole,
                    isCurrentUserCreator = isCurrentUserCreator,
                    onEditClick = {
                        id?.let(onEditClick)
                    },
                    onDeleteClick = {
                        id?.let(onDeleteClick)
                    },
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )
        Text(
            text = comment.text.orEmpty(),
            modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        )
    }
}

@Composable
private fun formatDate(dateTime: LocalDateTime): String {
    val relativeDate = DateTimeUtils.getRelativeDateStatus(dateTime.date)

    return when (relativeDate) {
        RelativeDate.TODAY -> {
            dateTime.format(
                LocalDateTime.Format {
                    time(
                        LocalTime.Format {
                            amPmHour()
                            char(':')
                            minute()
                            char(' ')
                            amPmMarker("AM", "PM")
                        },
                    )
                },
            )
        }

        RelativeDate.YESTERDAY -> {
            stringResource(id = R.string.yesterday)
        }

        else -> {
            dateTime.format(
                LocalDateTime.Format {
                    date(
                        LocalDate.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            dayOfMonth()
                            if (!DateTimeUtils.isThisYear(dateTime.date)) {
                                chars(", ")
                                year()
                            }
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun MenuButton(
    itemUserRole: UserRole,
    currentUserRole: CourseUserRole,
    isCurrentUserCreator: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
            when (currentUserRole) {
                CourseUserRole.Student -> {
                    if (isCurrentUserCreator) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.delete))
                            },
                            onClick = {
                                expanded = false
                                onDeleteClick()
                            },
                        )
                    }
                }

                is CourseUserRole.Teacher -> {
                    if (itemUserRole == UserRole.TEACHER) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = R.string.edit))
                            },
                            onClick = {
                                expanded = false
                                onEditClick()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(id = R.string.delete))
                        },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        },
                    )
                }
            }
        }
    }
}
