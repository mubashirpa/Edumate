package edumate.app.presentation.classwork.components

import android.text.format.DateUtils
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
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.presentation.components.FilledTonalIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import edumate.app.R.string as Strings

@Composable
fun CourseWorkListItem(
    courseWork: CourseWork,
    modifier: Modifier = Modifier,
    isTeacher: Boolean,
    workType: CourseWorkType,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    ClassworkListItemContent(
        title = courseWork.title.orEmpty(),
        modifier = modifier.clickable(onClick = onClick),
        leadingIcon =
            when (workType) {
                CourseWorkType.MULTIPLE_CHOICE_QUESTION -> Icons.AutoMirrored.Outlined.LiveHelp
                CourseWorkType.SHORT_ANSWER_QUESTION -> Icons.AutoMirrored.Outlined.LiveHelp
                else -> Icons.AutoMirrored.Outlined.Assignment
            },
        isMaterial = false,
        isTeacher = isTeacher,
        // TODO
        creationTime = null,
        // TODO
        dueTime = null,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
    )
}

@Composable
private fun ClassworkListItemContent(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector,
    isMaterial: Boolean,
    isTeacher: Boolean,
    creationTime: Date? = null,
    dueTime: Date? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateTimeFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
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
        supportingContent =
            if (isTeacher) {
                if (creationTime != null) {
                    {
                        val isToday = DateUtils.isToday(creationTime.time)
                        val posted =
                            if (isToday) {
                                timeFormat.format(creationTime)
                            } else {
                                dateFormat.format(creationTime)
                            }
                        Text(text = stringResource(id = Strings.posted_, posted))
                    }
                } else {
                    null
                }
            } else {
                if (isMaterial) {
                    if (creationTime != null) {
                        {
                            val isToday = DateUtils.isToday(creationTime.time)
                            val posted =
                                if (isToday) {
                                    timeFormat.format(creationTime)
                                } else {
                                    dateTimeFormat.format(creationTime)
                                }
                            Text(text = stringResource(id = Strings.posted_, posted))
                        }
                    } else {
                        null
                    }
                } else {
                    {
                        if (dueTime != null) {
                            val isToday = DateUtils.isToday(dueTime.time)
                            val due =
                                if (isToday) {
                                    stringResource(
                                        id = Strings.due_today_,
                                        timeFormat.format(dueTime),
                                    )
                                } else {
                                    stringResource(
                                        id = Strings.due_,
                                        dateTimeFormat.format(dueTime),
                                    )
                                }
                            Text(text = due)
                        } else {
                            Text(text = stringResource(id = Strings.no_due_date))
                        }
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
