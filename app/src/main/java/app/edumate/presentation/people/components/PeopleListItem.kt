package app.edumate.presentation.people.components

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
import androidx.compose.ui.tooling.preview.Preview
import app.edumate.R
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.courseDetails.CurrentUserRole
import app.edumate.presentation.theme.EdumateTheme

@Composable
fun PeopleListItem(
    user: User?,
    role: UserRole,
    courseOwnerId: String,
    currentUserId: String,
    currentUserRole: CurrentUserRole,
    onEmailUserClick: (email: String) -> Unit,
    onLeaveClassClick: () -> Unit,
    onRemoveUserClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemUserId = user?.id
    val isCurrentUser = currentUserId == itemUserId
    val itemUserRole =
        when {
            courseOwnerId == itemUserId -> ItemUserRole.OWNER
            role == UserRole.TEACHER -> ItemUserRole.TEACHER
            else -> ItemUserRole.STUDENT
        }

    PeopleListItemContent(
        userId = itemUserId.orEmpty(),
        name = user?.name.orEmpty(),
        photoUrl = user?.avatarUrl,
        currentUserRole = currentUserRole,
        itemUserRole = itemUserRole,
        isCurrentUser = isCurrentUser,
        onEmailUserClick = {
            user?.email?.let(onEmailUserClick)
        },
        onLeaveClassClick = onLeaveClassClick,
        onRemoveUserClick = onRemoveUserClick,
        modifier = modifier,
    )
}

@Composable
private fun PeopleListItemContent(
    userId: String,
    name: String,
    photoUrl: String?,
    currentUserRole: CurrentUserRole,
    itemUserRole: ItemUserRole,
    isCurrentUser: Boolean,
    onEmailUserClick: () -> Unit,
    onLeaveClassClick: () -> Unit,
    onRemoveUserClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hideTrailingContent =
        (currentUserRole == CurrentUserRole.OWNER && itemUserRole == ItemUserRole.OWNER) ||
            (currentUserRole == CurrentUserRole.STUDENT)
    val trailingContent: @Composable (() -> Unit)? =
        if (hideTrailingContent) {
            null
        } else {
            {
                PeopleMenuButton(
                    currentUserRole = currentUserRole,
                    itemUserRole = itemUserRole,
                    isCurrentUser = isCurrentUser,
                    onEmailUserClick = onEmailUserClick,
                    onLeaveClassClick = onLeaveClassClick,
                    onRemoveUserClick = onRemoveUserClick,
                )
            }
        }

    ListItem(
        headlineContent = {
            Text(text = name)
        },
        modifier = modifier,
        leadingContent = {
            UserAvatar(
                id = userId,
                fullName = name,
                photoUrl = photoUrl,
            )
        },
        trailingContent = trailingContent,
    )
}

@Composable
private fun PeopleMenuButton(
    currentUserRole: CurrentUserRole,
    itemUserRole: ItemUserRole,
    isCurrentUser: Boolean,
    onEmailUserClick: () -> Unit,
    onLeaveClassClick: () -> Unit,
    onRemoveUserClick: () -> Unit,
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
                CurrentUserRole.OWNER -> {
                    when (itemUserRole) {
                        ItemUserRole.TEACHER -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.email))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailUserClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.remove))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveUserClick()
                                },
                            )
                        }

                        ItemUserRole.STUDENT -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.email_student))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailUserClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.remove_student))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveUserClick()
                                },
                            )
                        }

                        else -> {
                            // Nothing shown
                        }
                    }
                }

                CurrentUserRole.TEACHER -> {
                    when (itemUserRole) {
                        ItemUserRole.OWNER -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.email))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailUserClick()
                                },
                            )
                        }

                        ItemUserRole.TEACHER -> {
                            if (isCurrentUser) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.leave_class))
                                    },
                                    onClick = {
                                        expanded = false
                                        onLeaveClassClick()
                                    },
                                )
                            } else {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.email))
                                    },
                                    onClick = {
                                        expanded = false
                                        onEmailUserClick()
                                    },
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = R.string.remove))
                                    },
                                    onClick = {
                                        expanded = false
                                        onRemoveUserClick()
                                    },
                                )
                            }
                        }

                        ItemUserRole.STUDENT -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.email_student))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailUserClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.remove_student))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveUserClick()
                                },
                            )
                        }
                    }
                }

                CurrentUserRole.STUDENT -> {
                    // Nothing shown
                }
            }
        }
    }
}

@Preview
@Composable
private fun PeopleListItemPreview() {
    EdumateTheme {
        PeopleListItemContent(
            userId = "user",
            name = "User",
            photoUrl = null,
            currentUserRole = CurrentUserRole.OWNER,
            itemUserRole = ItemUserRole.TEACHER,
            isCurrentUser = false,
            onEmailUserClick = {},
            onLeaveClassClick = {},
            onRemoveUserClick = {},
        )
    }
}

private enum class ItemUserRole {
    OWNER,
    TEACHER,
    STUDENT,
}
