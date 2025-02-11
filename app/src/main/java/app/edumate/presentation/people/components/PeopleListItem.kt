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
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.theme.EdumateTheme

@Composable
fun PeopleListItem(
    person: User?,
    role: UserRole,
    courseOwnerId: String,
    currentUserId: String,
    currentUserRole: CourseUserRole,
    onChangePersonRole: (userId: String, role: UserRole) -> Unit,
    onEmailUserClick: (email: String) -> Unit,
    onLeaveClassClick: () -> Unit,
    onRemoveUserClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemUserId = person?.id
    val isCurrentUserItem = currentUserId == itemUserId
    val itemUserRole =
        when (role) {
            UserRole.STUDENT -> PeopleUserRole.Student
            UserRole.TEACHER -> PeopleUserRole.Teacher(isCourseOwner = courseOwnerId == itemUserId)
        }

    PeopleListItemContent(
        userId = itemUserId.orEmpty(),
        name = person?.name.orEmpty(),
        photoUrl = person?.photoUrl,
        currentUserRole = currentUserRole,
        itemUserRole = itemUserRole,
        isCurrentUserItem = isCurrentUserItem,
        onMakeTeacherClick = {
            itemUserId?.let {
                onChangePersonRole(it, UserRole.TEACHER)
            }
        },
        onDismissAsTeacherClick = {
            itemUserId?.let {
                onChangePersonRole(it, UserRole.STUDENT)
            }
        },
        onEmailUserClick = {
            person?.email?.let(onEmailUserClick)
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
    currentUserRole: CourseUserRole,
    itemUserRole: PeopleUserRole,
    isCurrentUserItem: Boolean,
    onMakeTeacherClick: () -> Unit,
    onDismissAsTeacherClick: () -> Unit,
    onEmailUserClick: () -> Unit,
    onLeaveClassClick: () -> Unit,
    onRemoveUserClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hideTrailingContent =
        isCurrentUserItem &&
            (
                currentUserRole == CourseUserRole.Student ||
                    currentUserRole == CourseUserRole.Teacher(true)
            )
    val trailingContent: @Composable (() -> Unit)? =
        if (hideTrailingContent) {
            null
        } else {
            {
                MenuButton(
                    currentUserRole = currentUserRole,
                    itemUserRole = itemUserRole,
                    isCurrentUserItem = isCurrentUserItem,
                    onMakeTeacherClick = onMakeTeacherClick,
                    onDismissAsTeacherClick = onDismissAsTeacherClick,
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
        overlineContent = {
            if (itemUserRole is PeopleUserRole.Teacher) {
                val text =
                    if (itemUserRole.isCourseOwner) {
                        stringResource(id = R.string.admin)
                    } else {
                        stringResource(id = R.string.teacher)
                    }
                Text(text = text)
            }
        },
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
private fun MenuButton(
    currentUserRole: CourseUserRole,
    itemUserRole: PeopleUserRole,
    isCurrentUserItem: Boolean,
    onMakeTeacherClick: () -> Unit,
    onDismissAsTeacherClick: () -> Unit,
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
                CourseUserRole.Student -> {
                    if (!isCurrentUserItem) {
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
                }

                is CourseUserRole.Teacher -> {
                    when (itemUserRole) {
                        PeopleUserRole.Student -> {
                            if (currentUserRole.isCourseOwner) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(R.string.make_teacher))
                                    },
                                    onClick = {
                                        expanded = false
                                        onMakeTeacherClick()
                                    },
                                )
                            }
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

                        is PeopleUserRole.Teacher -> {
                            when {
                                itemUserRole.isCourseOwner -> {
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

                                isCurrentUserItem -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = stringResource(id = R.string.leave_class))
                                        },
                                        onClick = {
                                            expanded = false
                                            onLeaveClassClick()
                                        },
                                    )
                                }

                                else -> {
                                    if (currentUserRole.isCourseOwner) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(text = stringResource(R.string.dismiss_as_teacher))
                                            },
                                            onClick = {
                                                expanded = false
                                                onDismissAsTeacherClick()
                                            },
                                        )
                                    }
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
                        }
                    }
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
            currentUserRole = CourseUserRole.Teacher(true),
            itemUserRole = PeopleUserRole.Teacher(true),
            isCurrentUserItem = false,
            onMakeTeacherClick = {},
            onDismissAsTeacherClick = {},
            onEmailUserClick = {},
            onLeaveClassClick = {},
            onRemoveUserClick = {},
        )
    }
}

sealed class PeopleUserRole {
    data class Teacher(
        val isCourseOwner: Boolean,
    ) : PeopleUserRole()

    data object Student : PeopleUserRole()
}
