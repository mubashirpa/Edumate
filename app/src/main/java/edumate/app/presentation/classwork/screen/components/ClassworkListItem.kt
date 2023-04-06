package edumate.app.presentation.classwork.screen.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.LiveHelp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.UserType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClassworkListItem(
    work: CourseWork,
    modifier: Modifier = Modifier,
    currentUserType: UserType,
    workType: CourseWorkType,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateTimeFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val trailingContent: @Composable (() -> Unit)? = if (currentUserType == UserType.TEACHER) {
        {
            MenuButton(
                onEdit = onEdit,
                onDelete = onDelete
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
                UserType.UNKNOWN -> {}
            }
        },
        leadingContent = {
            val icon = when (workType) {
                CourseWorkType.ASSIGNMENT -> Icons.Outlined.Assignment
                CourseWorkType.MULTIPLE_CHOICE_QUESTION -> Icons.Outlined.LiveHelp
                CourseWorkType.SHORT_ANSWER_QUESTION -> Icons.Outlined.LiveHelp
                else -> Icons.Outlined.Book
            }
            LeadingIcon(imageVector = icon)
        },
        trailingContent = trailingContent
    )
}

@Composable
private fun LeadingIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(40.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun MenuButton(
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = Strings.delete)) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}