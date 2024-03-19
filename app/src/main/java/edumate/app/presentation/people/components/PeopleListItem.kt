package edumate.app.presentation.people.components

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.model.userProfiles.UserProfile
import edumate.app.presentation.components.UserAvatar
import edumate.app.R.string as Strings

@Composable
fun PeopleListItem(
    profile: UserProfile?,
    modifier: Modifier = Modifier,
    course: Course,
    userId: String,
    onEmailClick: () -> Unit,
    onLeaveClassClick: () -> Unit,
    onMakeClassOwnerClick: (profile: UserProfile) -> Unit,
    onRemoveClick: (profile: UserProfile) -> Unit,
) {
    val userRole =
        when {
            course.ownerId == userId -> UserRole.OWNER
            course.teachers?.any { it.userId == userId } == true -> UserRole.TEACHER
            else -> UserRole.STUDENT
        }
    val profileId = profile?.id
    val targetUserRole =
        when {
            course.ownerId == profileId -> TargetUserRole.OWNER
            course.teachers?.any { it.userId == profileId } == true -> TargetUserRole.TEACHER
            course.students?.any { it.userId == profileId } == true -> TargetUserRole.STUDENT
            else -> TargetUserRole.INVITED
        }
    val isMe = userId == profileId

    PeopleListItemContent(
        userId = profileId.orEmpty(),
        name = profile?.name?.fullName.orEmpty(),
        photoUrl = profile?.photoUrl,
        userRole = userRole,
        targetUserRole = targetUserRole,
        isMe = isMe,
        modifier = modifier,
        onEmailClick = onEmailClick,
        onLeaveClassClick = onLeaveClassClick,
        onMakeClassOwnerClick = {
            if (profile != null) {
                onMakeClassOwnerClick(profile)
            }
        },
        onRemoveClick = {
            if (profile != null) {
                onRemoveClick(profile)
            }
        },
    )
}

@Composable
private fun PeopleListItemContent(
    userId: String,
    name: String,
    photoUrl: String?,
    userRole: UserRole,
    targetUserRole: TargetUserRole,
    isMe: Boolean,
    modifier: Modifier = Modifier,
    onEmailClick: () -> Unit,
    onLeaveClassClick: () -> Unit,
    onMakeClassOwnerClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    val hideTrailingContent =
        (userRole == UserRole.OWNER && targetUserRole == TargetUserRole.OWNER) || (userRole == UserRole.STUDENT && isMe)
    val trailingContent: @Composable (() -> Unit)? =
        if (hideTrailingContent) {
            null
        } else {
            {
                MenuButton(
                    userRole = userRole,
                    targetUserRole = targetUserRole,
                    isMe = isMe,
                    onEmailClick = onEmailClick,
                    onLeaveClassClick = onLeaveClassClick,
                    onMakeClassOwnerClick = onMakeClassOwnerClick,
                    onRemoveClick = onRemoveClick,
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
private fun MenuButton(
    userRole: UserRole,
    targetUserRole: TargetUserRole,
    isMe: Boolean,
    onEmailClick: () -> Unit,
    onLeaveClassClick: () -> Unit,
    onMakeClassOwnerClick: () -> Unit,
    onRemoveClick: () -> Unit,
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
            when (userRole) {
                UserRole.OWNER -> {
                    when (targetUserRole) {
                        TargetUserRole.TEACHER -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.email))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.remove))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.make_class_owner))
                                },
                                onClick = {
                                    expanded = false
                                    onMakeClassOwnerClick()
                                },
                            )
                        }

                        TargetUserRole.STUDENT -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.email_student))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.remove_student))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveClick()
                                },
                            )
                        }

                        TargetUserRole.INVITED -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.email))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.remove))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveClick()
                                },
                            )
                        }

                        else -> {
                            // Nothing shown
                        }
                    }
                }

                UserRole.TEACHER -> {
                    when (targetUserRole) {
                        TargetUserRole.OWNER -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.email))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailClick()
                                },
                            )
                        }

                        TargetUserRole.TEACHER -> {
                            if (isMe) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = Strings.leave_class))
                                    },
                                    onClick = {
                                        expanded = false
                                        onLeaveClassClick()
                                    },
                                )
                            } else {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = Strings.email))
                                    },
                                    onClick = {
                                        expanded = false
                                        onEmailClick()
                                    },
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(id = Strings.remove))
                                    },
                                    onClick = {
                                        expanded = false
                                        onRemoveClick()
                                    },
                                )
                            }
                        }

                        TargetUserRole.STUDENT -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.email_student))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.remove_student))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveClick()
                                },
                            )
                        }

                        TargetUserRole.INVITED -> {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.email))
                                },
                                onClick = {
                                    expanded = false
                                    onEmailClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = Strings.remove))
                                },
                                onClick = {
                                    expanded = false
                                    onRemoveClick()
                                },
                            )
                        }
                    }
                }

                UserRole.STUDENT -> {
                    if (!isMe) {
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = Strings.email))
                            },
                            onClick = {
                                expanded = false
                                onEmailClick()
                            },
                        )
                    }
                    // Else nothing shown
                }
            }
        }
    }
}

@Preview
@Composable
private fun PeopleListItemPreview(
    @PreviewParameter(PersonName::class) name: String,
) {
    PeopleListItemContent(
        userId = name,
        name = name,
        photoUrl = null,
        userRole = UserRole.OWNER,
        targetUserRole = TargetUserRole.TEACHER,
        isMe = false,
        onEmailClick = {},
        onLeaveClassClick = {},
        onMakeClassOwnerClick = {},
        onRemoveClick = {},
    )
}

private enum class UserRole {
    OWNER,
    TEACHER,
    STUDENT,
}

private enum class TargetUserRole {
    OWNER,
    TEACHER,
    STUDENT,
    INVITED,
}

private class PersonName : LoremIpsum(2)
