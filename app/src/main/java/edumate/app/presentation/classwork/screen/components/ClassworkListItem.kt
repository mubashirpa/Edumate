package edumate.app.presentation.classwork.screen.components

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.LiveHelp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Book
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import edumate.app.R.string as Strings
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.FilledTonalIcon
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ClassworkListItem(
    work: CourseWork,
    modifier: Modifier = Modifier,
    currentUserType: UserType,
    workType: CourseWorkType,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateTimeFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val trailingContent: @Composable (() -> Unit)? = if (currentUserType == UserType.TEACHER) {
        {
            MenuButton(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    } else {
        null
    }

    ListItem(
        headlineContent = {
            Text(
                text = work.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        modifier = modifier.clickable(onClick = onClick),
        supportingContent = {
            when (currentUserType) {
                UserType.STUDENT -> {
                    if (work.workType == CourseWorkType.MATERIAL) {
                        val creationTime = work.creationTime
                        if (creationTime != null) {
                            val isToday = DateUtils.isToday(creationTime.time)
                            val posted = if (isToday) {
                                timeFormat.format(creationTime)
                            } else {
                                dateTimeFormat.format(creationTime)
                            }
                            Text(text = stringResource(id = Strings.posted_, posted))
                        }
                    } else {
                        val dueTime = work.dueTime
                        if (dueTime != null) {
                            val isToday = DateUtils.isToday(dueTime.time)
                            val due = if (isToday) {
                                stringResource(id = Strings.due_today_, timeFormat.format(dueTime))
                            } else {
                                stringResource(id = Strings.due_, dateTimeFormat.format(dueTime))
                            }
                            Text(text = due)
                        } else {
                            Text(text = stringResource(id = Strings.no_due_date))
                        }
                    }
                }

                UserType.TEACHER -> {
                    val creationTime = work.creationTime
                    if (creationTime != null) {
                        val isToday = DateUtils.isToday(creationTime.time)
                        val posted = if (isToday) {
                            timeFormat.format(creationTime)
                        } else {
                            dateFormat.format(creationTime)
                        }
                        Text(text = stringResource(id = Strings.posted_, posted))
                    }
                }
            }
        },
        leadingContent = {
            val icon = when (workType) {
                CourseWorkType.ASSIGNMENT -> Icons.AutoMirrored.Outlined.Assignment
                CourseWorkType.MULTIPLE_CHOICE_QUESTION -> Icons.AutoMirrored.Outlined.LiveHelp
                CourseWorkType.SHORT_ANSWER_QUESTION -> Icons.AutoMirrored.Outlined.LiveHelp
                else -> Icons.Outlined.Book
            }
            FilledTonalIcon(imageVector = icon)
        },
        trailingContent = trailingContent
    )
}

@Composable
private fun MenuButton(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
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
                text = { Text(text = stringResource(id = Strings.edit)) },
                onClick = {
                    expanded = false
                    onEditClick()
                }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = Strings.delete)) },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}