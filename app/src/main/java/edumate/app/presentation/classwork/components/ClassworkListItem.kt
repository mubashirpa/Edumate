package edumate.app.presentation.classwork.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.LiveHelp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.domain.model.classroom.courseWork.DueDate
import edumate.app.domain.model.classroom.courseWork.DueTime
import edumate.app.presentation.components.FilledTonalIcon
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import edumate.app.R.string as Strings

@Composable
fun ClassworkListItem(
    courseWork: CourseWork,
    modifier: Modifier = Modifier,
    isTeacher: Boolean,
    workType: CourseWorkType,
    onClick: (id: String, workType: CourseWorkType) -> Unit,
    onEditClick: (id: String, workType: CourseWorkType) -> Unit,
    onDeleteClick: (courseWork: CourseWork) -> Unit,
) {
    val id = courseWork.id

    ClassworkListItemContent(
        title = courseWork.title.orEmpty(),
        modifier =
            modifier.clickable {
                id?.let {
                    onClick(it, workType)
                }
            },
        leadingIcon =
            when (workType) {
                CourseWorkType.MULTIPLE_CHOICE_QUESTION -> Icons.AutoMirrored.Outlined.LiveHelp
                CourseWorkType.SHORT_ANSWER_QUESTION -> Icons.AutoMirrored.Outlined.LiveHelp
                else -> Icons.AutoMirrored.Outlined.Assignment
            },
        isMaterial = false,
        isTeacher = isTeacher,
        creationTime = courseWork.creationTime.orEmpty(),
        dueDate = courseWork.dueDate,
        dueTime = courseWork.dueTime,
        onEditClick = {
            id?.let {
                onEditClick(it, workType)
            }
        },
        onDeleteClick = {
            onDeleteClick(courseWork)
        },
    )
}

@Composable
private fun ClassworkListItemContent(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector,
    isMaterial: Boolean,
    isTeacher: Boolean,
    creationTime: String,
    dueDate: DueDate? = null,
    dueTime: DueTime? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val creationDateTime =
        remember {
            try {
                Instant.parse(creationTime).toLocalDateTime(TimeZone.currentSystemDefault())
            } catch (e: Exception) {
                LocalDateTime(0, 1, 1, 0, 0, 0, 0)
            }
        }
    val posted =
        remember {
            if (isToday(creationDateTime.date)) {
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
            } else {
                creationDateTime.format(
                    LocalDateTime.Format {
                        date(
                            LocalDate.Format {
                                monthName(MonthNames.ENGLISH_ABBREVIATED)
                                char(' ')
                                dayOfMonth()
                                chars(", ")
                                year()
                            },
                        )
                    },
                )
            }
        }
    val trailingContent: @Composable (() -> Unit)? =
        if (isTeacher) {
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
            if (isTeacher || isMaterial) {
                Text(text = stringResource(id = Strings.posted_, posted))
            } else {
                if (dueDate != null) {
                    val dueDateTime =
                        remember {
                            LocalDateTime(
                                dueDate.year ?: 0,
                                dueDate.month ?: 1,
                                dueDate.day ?: 1,
                                dueTime?.hours ?: 0,
                                dueTime?.minutes ?: 0,
                                dueTime?.seconds ?: 0,
                                dueTime?.nanos ?: 0,
                            )
                        }
                    val due =
                        if (isToday(dueDateTime.date)) {
                            val formattedDue =
                                dueDateTime.format(
                                    LocalDateTime.Format {
                                        time(
                                            LocalTime.Format {
                                                hour()
                                                char(':')
                                                minute()
                                                char(' ')
                                                amPmMarker("AM", "PM")
                                            },
                                        )
                                    },
                                )
                            stringResource(id = Strings.due_today_, formattedDue)
                        } else {
                            val isThisYear = isThisYear(dueDateTime.date)
                            val formattedDue =
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
                            stringResource(id = Strings.due_, formattedDue)
                        }
                    Text(text = due)
                } else {
                    Text(text = stringResource(id = Strings.no_due_date))
                }
            }
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
                    Text(text = stringResource(id = Strings.edit))
                },
                onClick = {
                    expanded = false
                    onEditClick()
                },
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = Strings.delete))
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                },
            )
        }
    }
}

@Preview
@Composable
private fun ClassworkListItemPreview(
    @PreviewParameter(LoremIpsum::class) title: String,
) {
    ClassworkListItemContent(
        title = title,
        leadingIcon = Icons.AutoMirrored.Outlined.Assignment,
        isMaterial = false,
        isTeacher = true,
        creationTime = "2024-03-05T16:29:37Z",
        dueDate = DueDate(8, 2, 2024),
        dueTime = DueTime(),
        onEditClick = {},
        onDeleteClick = {},
    )
}

private fun isToday(date: LocalDate): Boolean {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return date == today
}

private fun isThisYear(date: LocalDate): Boolean {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return date.year == today.year
}
