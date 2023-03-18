package edumate.app.presentation.classwork.screen.components

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
import androidx.compose.ui.unit.dp
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.UserType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClassworkListItem(
    onClick: () -> Unit,
    userType: UserType,
    workType: CourseWorkType,
    work: CourseWork
) {
    val trailingContent: @Composable (() -> Unit)? = if (userType == UserType.TEACHER) {
        { MenuButton() }
    } else {
        null
    }

    ListItem(
        headlineContent = {
            Text(text = work.title)
        },
        modifier = Modifier.clickable(onClick = onClick),
        supportingContent = {
            val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val postedDate: String = format.format(work.creationTime ?: 0)
            Text(text = "Posted $postedDate")
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
private fun MenuButton() {
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
                text = { Text(text = "Edit") },
                onClick = {
                    expanded = false
                    // TODO("Not yet implemented")
                }
            )
            DropdownMenuItem(
                text = { Text(text = "Delete") },
                onClick = {
                    expanded = false
                    // TODO("Not yet implemented")
                }
            )
        }
    }
}