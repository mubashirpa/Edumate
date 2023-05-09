package edumate.app.presentation.people.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.UserAvatar

@Composable
fun PeopleListItem(
    userProfile: UserProfile,
    modifier: Modifier = Modifier,
    currentUserId: String,
    currentUserType: UserType,
    courseOwnerId: String,
    onLeaveClassClick: () -> Unit,
    onEmailClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val userId = userProfile.id
    val trailingContent: @Composable (() -> Unit)? =
        if (currentUserType == UserType.TEACHER) {
            // Current userProfile is a teacher
            val isCurrentUser = userId == currentUserId
            val isCourseOwner = userId == courseOwnerId
            if (isCurrentUser && isCourseOwner) {
                null
            } else {
                {
                    MenuButton(
                        isCurrentUser = isCurrentUser,
                        isCourseOwner = isCourseOwner,
                        onLeaveClassClick = onLeaveClassClick,
                        onEmailClick = onEmailClick,
                        onRemoveClick = onRemoveClick
                    )
                }
            }
        } else {
            null
        }

    ListItem(
        headlineContent = {
            Text(text = userProfile.displayName.orEmpty())
        },
        modifier = modifier,
        leadingContent = {
            UserAvatar(
                id = userId,
                fullName = userProfile.displayName ?: userProfile.emailAddress.orEmpty(),
                photoUrl = userProfile.photoUrl
            )
        },
        trailingContent = trailingContent
    )
}

@Composable
private fun MenuButton(
    isCurrentUser: Boolean,
    isCourseOwner: Boolean,
    onLeaveClassClick: () -> Unit,
    onEmailClick: () -> Unit,
    onRemoveClick: () -> Unit
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
            if (isCurrentUser) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.leave_class)) },
                    onClick = {
                        expanded = false
                        onLeaveClassClick()
                    }
                )
            } else if (isCourseOwner) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.email)) },
                    onClick = {
                        expanded = false
                        onEmailClick()
                    }
                )
            } else {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.email)) },
                    onClick = {
                        expanded = false
                        onEmailClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.remove)) },
                    onClick = {
                        expanded = false
                        onRemoveClick()
                    }
                )
            }
        }
    }
}