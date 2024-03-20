package edumate.app.presentation.stream.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edumate.app.core.utils.DateTimeUtils
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.core.utils.RelativeDate
import edumate.app.domain.model.classroom.Material
import edumate.app.domain.model.classroom.announcements.Announcement
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.model.userProfiles.Name
import edumate.app.domain.model.userProfiles.UserProfile
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import edumate.app.R.string as Strings

@Composable
fun AnnouncementListItem(
    announcement: Announcement,
    modifier: Modifier = Modifier,
    course: Course,
    userId: String,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: (id: String) -> Unit,
    onCopyLinkClick: (link: String) -> Unit,
    onClick: (id: String) -> Unit,
) {
    val id = announcement.id
    val userRole =
        when {
            course.teachers?.any { it.userId == userId } == true -> UserRole.TEACHER
            else -> UserRole.STUDENT
        }
    val targetUserRole =
        when {
            course.teachers?.any { it.userId == announcement.creatorUserId } == true -> UserRole.TEACHER
            else -> UserRole.STUDENT
        }

    AnnouncementListItemContent(
        text = announcement.text.orEmpty(),
        modifier = modifier,
        materials = announcement.materials.orEmpty(),
        isCreator = announcement.creatorUserId == userId,
        creator = announcement.creator,
        creationTime = announcement.creationTime.orEmpty(),
        updateTime = announcement.updateTime.orEmpty(),
        userRole = userRole,
        targetUserRole = targetUserRole,
        onEditClick = {
            id?.let(onEditClick)
        },
        onDeleteClick = {
            id?.let(onDeleteClick)
        },
        onCopyLinkClick = {
            announcement.alternateLink?.let(onCopyLinkClick)
        },
        onClick = {
            id?.let(onClick)
        },
    )
}

@Composable
private fun AnnouncementListItemContent(
    text: String,
    modifier: Modifier = Modifier,
    materials: List<Material>,
    isCreator: Boolean,
    creator: UserProfile?,
    creationTime: String,
    updateTime: String,
    userRole: UserRole,
    targetUserRole: UserRole,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val fileUtils =
        remember {
            FileUtils(context)
        }
    val systemTimeZone = TimeZone.currentSystemDefault()
    val creationDateTime =
        remember {
            try {
                Instant.parse(creationTime).toLocalDateTime(systemTimeZone)
            } catch (e: Exception) {
                null
            }
        }
    val updateDateTime =
        try {
            Instant.parse(updateTime).toLocalDateTime(systemTimeZone)
        } catch (e: Exception) {
            null
        }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        border =
            BorderStroke(
                width = Dp.Hairline,
                color = MaterialTheme.colorScheme.outline,
            ),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = creator?.name?.fullName.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            supportingContent = {
                if (creationDateTime != null) {
                    val formattedDateTime =
                        formatDate(
                            creationDateTime = creationDateTime,
                            updateDateTime = updateDateTime,
                        )

                    Text(
                        text = formattedDateTime,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            },
            leadingContent = {
                UserAvatar(
                    id = creator?.id.orEmpty(),
                    fullName = creator?.name?.fullName.orEmpty(),
                    photoUrl = creator?.photoUrl,
                )
            },
            trailingContent = {
                MenuButton(
                    userRole = userRole,
                    targetUserRole = targetUserRole,
                    isCreator = isCreator,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onCopyLinkClick = onCopyLinkClick,
                )
            },
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            if (materials.isNotEmpty()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    materials.forEach { material ->
                        when {
                            material.driveFile != null -> {
                                val uri =
                                    remember {
                                        Uri.parse(material.driveFile.alternateLink)
                                    }
                                val mimeType =
                                    remember {
                                        fileUtils.getMimeType(uri)
                                    }
                                val icon =
                                    remember {
                                        when (fileUtils.getFileType(mimeType)) {
                                            FileType.IMAGE -> Icons.Default.Image
                                            FileType.VIDEO -> Icons.Default.VideoFile
                                            FileType.AUDIO -> Icons.Default.AudioFile
                                            FileType.PDF -> Icons.Default.PictureAsPdf
                                            FileType.UNKNOWN -> Icons.AutoMirrored.Filled.InsertDriveFile
                                        }
                                    }

                                AssistChip(
                                    onClick = {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(browserIntent)
                                    },
                                    label = {
                                        Text(
                                            text = material.driveFile.title.orEmpty(),
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                        )
                                    },
                                    modifier = Modifier.widthIn(max = 180.dp),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                        )
                                    },
                                )
                            }

                            material.link != null -> {
                                AssistChip(
                                    onClick = {
                                        val browserIntent =
                                            Intent(Intent.ACTION_VIEW, Uri.parse(material.link.url))
                                        context.startActivity(browserIntent)
                                    },
                                    label = {
                                        Text(
                                            text = material.link.title.orEmpty(),
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                        )
                                    },
                                    modifier = Modifier.widthIn(max = 180.dp),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Link,
                                            contentDescription = null,
                                        )
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

@Composable
private fun MenuButton(
    userRole: UserRole,
    targetUserRole: UserRole,
    isCreator: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            when (userRole) {
                UserRole.TEACHER -> {
                    if (targetUserRole == UserRole.TEACHER) {
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = Strings.edit))
                            },
                            onClick = {
                                expanded = false
                                onEditClick()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = Strings.delete))
                        },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = Strings.copy_link))
                        },
                        onClick = {
                            expanded = false
                            onCopyLinkClick()
                        },
                    )
                }

                UserRole.STUDENT -> {
                    if (isCreator) {
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = Strings.delete))
                            },
                            onClick = {
                                expanded = false
                                onDeleteClick()
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = Strings.copy_link))
                            },
                            onClick = {
                                expanded = false
                                onCopyLinkClick()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formatDate(
    creationDateTime: LocalDateTime,
    updateDateTime: LocalDateTime?,
): String {
    val posted = formatDate(dateTime = creationDateTime)
    val edited = updateDateTime?.let { formatDate(dateTime = it) }

    return if (updateDateTime != null && creationDateTime.compareTo(updateDateTime) == 0) {
        posted
    } else {
        stringResource(
            id = Strings.posted_edited_,
            posted,
            edited.toString(),
        )
    }
}

@Composable
private fun formatDate(dateTime: LocalDateTime): String {
    val relativeDate = DateTimeUtils.getRelativeDateStatus(dateTime.date)

    return when (relativeDate) {
        RelativeDate.TODAY -> {
            dateTime.format(
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
        }

        RelativeDate.YESTERDAY -> {
            stringResource(id = Strings.yesterday)
        }

        else -> {
            dateTime.format(
                LocalDateTime.Format {
                    date(
                        LocalDate.Format {
                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                            char(' ')
                            dayOfMonth()
                            if (!DateTimeUtils.isThisYear(dateTime.date)) {
                                chars(", ")
                                year()
                            }
                        },
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun AnnouncementListItemPreview(
    @PreviewParameter(LoremIpsum::class) text: String,
) {
    EdumateTheme {
        AnnouncementListItemContent(
            text = text.take(100),
            materials = emptyList(),
            isCreator = true,
            creator = UserProfile(name = Name(fullName = "User")),
            creationTime = "2024-01-01T00:00:00Z",
            updateTime = "2024-01-01T01:00:00Z",
            userRole = UserRole.TEACHER,
            targetUserRole = UserRole.TEACHER,
            onEditClick = {},
            onDeleteClick = {},
            onCopyLinkClick = {},
            onClick = {},
        )
    }
}

private enum class UserRole {
    TEACHER,
    STUDENT,
}
