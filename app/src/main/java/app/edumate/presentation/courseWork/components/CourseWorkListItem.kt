package app.edumate.presentation.courseWork.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.HelpCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.utils.DateTimeUtils
import app.edumate.core.utils.RelativeDate
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.presentation.theme.EdumateTheme
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
fun CourseWorkListItem(
    onClick: (id: String) -> Unit,
    courseWork: CourseWork,
    workType: CourseWorkType,
    isCurrentUserTeacher: Boolean,
    onEditClick: (id: String, workType: CourseWorkType) -> Unit,
    onDeleteClick: (courseWork: CourseWork) -> Unit,
    modifier: Modifier = Modifier,
) {
    val id = courseWork.id

    ClassworkListItemContent(
        title = courseWork.title.orEmpty(),
        leadingIcon =
            when (workType) {
                CourseWorkType.ASSIGNMENT -> Icons.AutoMirrored.Outlined.Assignment
                CourseWorkType.MATERIAL -> Icons.Outlined.FilePresent
                else -> Icons.AutoMirrored.Outlined.HelpCenter
            },
        isMaterial = workType == CourseWorkType.MATERIAL,
        isCurrentUserTeacher = isCurrentUserTeacher,
        creationTime = courseWork.creationTime.orEmpty(),
        onEditClick = {
            id?.let {
                onEditClick(it, workType)
            }
        },
        onDeleteClick = {
            onDeleteClick(courseWork)
        },
        modifier =
            modifier.clickable(
                enabled = id != null,
                onClick = {
                    onClick(id!!)
                },
            ),
        dueTime = courseWork.dueTime,
    )
}

@Composable
private fun ClassworkListItemContent(
    title: String,
    leadingIcon: ImageVector,
    isMaterial: Boolean,
    isCurrentUserTeacher: Boolean,
    creationTime: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    dueTime: String? = null,
) {
    val creationDateTime =
        remember(creationTime) {
            Instant.parse(creationTime).toLocalDateTime(TimeZone.currentSystemDefault())
        }
    val trailingContent: @Composable (() -> Unit)? =
        if (isCurrentUserTeacher) {
            {
                MenuButton(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                )
            }
        } else {
            null
        }

    ListItem(
        headlineContent = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        modifier = modifier,
        supportingContent = {
            val formattedDateTime =
                formatDate(
                    creationDateTime = creationDateTime,
                    dueTime = dueTime,
                    isMaterial = isMaterial,
                    isCurrentUserTeacher = isCurrentUserTeacher,
                )

            Text(text = formattedDateTime)
        },
        leadingContent = {
            FilledTonalIcon(imageVector = leadingIcon)
        },
        trailingContent = trailingContent,
    )
}

@Composable
private fun MenuButton(
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
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.edit))
                },
                onClick = {
                    expanded = false
                    onEditClick()
                },
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.delete))
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                },
            )
        }
    }
}

@Composable
fun FilledTonalIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .size(40.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun formatDate(
    creationDateTime: LocalDateTime,
    dueTime: String?,
    isMaterial: Boolean,
    isCurrentUserTeacher: Boolean,
): String {
    val postedRelativeDate = DateTimeUtils.getRelativeDateStatus(creationDateTime.date)
    val posted =
        when (postedRelativeDate) {
            RelativeDate.TODAY -> {
                val postedTime =
                    creationDateTime.format(
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

                stringResource(id = R.string.posted_, postedTime)
            }

            RelativeDate.YESTERDAY -> {
                stringResource(id = R.string.posted_yesterday)
            }

            else -> {
                val postedDate =
                    creationDateTime.format(
                        LocalDateTime.Format {
                            date(
                                LocalDate.Format {
                                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                                    char(' ')
                                    dayOfMonth()
                                    if (!DateTimeUtils.isThisYear(creationDateTime.date)) {
                                        chars(", ")
                                        year()
                                    }
                                },
                            )
                        },
                    )

                stringResource(id = R.string.posted_, postedDate)
            }
        }
    val due =
        if (dueTime != null) {
            val instant = Instant.parse(dueTime)
            val dueDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val dueRelativeDate =
                DateTimeUtils.getRelativeDateStatus(dueDateTime.date)
            val formattedDueTime =
                dueDateTime.format(
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

            when (dueRelativeDate) {
                RelativeDate.TODAY -> {
                    if (isCurrentUserTeacher) {
                        stringResource(id = R.string.due_today)
                    } else {
                        stringResource(id = R.string.due_today_, formattedDueTime)
                    }
                }

                RelativeDate.TOMORROW -> {
                    if (isCurrentUserTeacher) {
                        stringResource(id = R.string.due_tomorrow)
                    } else {
                        stringResource(id = R.string.due_tomorrow_, formattedDueTime)
                    }
                }

                RelativeDate.YESTERDAY -> {
                    if (isCurrentUserTeacher) {
                        stringResource(id = R.string.due_yesterday)
                    } else {
                        stringResource(
                            id = R.string.due_yesterday_,
                            formattedDueTime,
                        )
                    }
                }

                RelativeDate.OTHER -> {
                    val isThisYear = DateTimeUtils.isThisYear(dueDateTime.date)
                    val formattedDueDate =
                        dueDateTime.format(
                            LocalDateTime.Format {
                                date(
                                    LocalDate.Format {
                                        monthName(MonthNames.ENGLISH_ABBREVIATED)
                                        char(' ')
                                        dayOfMonth()
                                        chars(", ")
                                        if (!isThisYear) {
                                            year()
                                        }
                                    },
                                )
                                if (isThisYear) {
                                    time(
                                        LocalTime.Format {
                                            amPmHour()
                                            char(':')
                                            minute()
                                            char(' ')
                                            amPmMarker("AM", "PM")
                                        },
                                    )
                                }
                            },
                        )

                    stringResource(id = R.string.due_, formattedDueDate)
                }
            }
        } else {
            if (isCurrentUserTeacher) {
                posted
            } else {
                stringResource(id = R.string.no_due_date)
            }
        }
    return if (isMaterial) posted else due
}

@Preview
@Composable
private fun ClassworkListItemPreview() {
    EdumateTheme {
        ClassworkListItemContent(
            title = "Classwork",
            leadingIcon = Icons.AutoMirrored.Outlined.Assignment,
            isMaterial = false,
            isCurrentUserTeacher = false,
            creationTime = "2025-01-28T08:26:42.830742+00:00",
            dueTime = "2025-01-28T12:15:58.233568+00:00",
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
