package app.edumate.presentation.stream.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.utils.DateTimeUtils
import app.edumate.core.utils.RelativeDate
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.model.material.Material
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Composable
fun AnnouncementListItem(
    announcement: Announcement,
    itemUserRole: UserRole,
    currentUserRole: CourseUserRole,
    currentUserId: String,
    selected: Boolean,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: (id: String) -> Unit,
    onCopyLinkClick: (link: String) -> Unit,
    onClearSelection: () -> Unit,
    onClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val id = announcement.id
    val isCurrentUserCreator = announcement.creatorUserId == currentUserId

    AnnouncementListItemContent(
        text = announcement.text.orEmpty(),
        materials = announcement.materials.orEmpty(),
        creationTime = announcement.creationTime.orEmpty(),
        updateTime = announcement.updateTime.orEmpty(),
        creator = announcement.creator,
        itemUserRole = itemUserRole,
        currentUserRole = currentUserRole,
        isCurrentUserCreator = isCurrentUserCreator,
        selected = selected,
        onEditClick = {
            id?.let(onEditClick)
        },
        onDeleteClick = {
            id?.let(onDeleteClick)
        },
        onCopyLinkClick = {
            announcement.alternateLink?.let(onCopyLinkClick)
        },
        onClearSelection = onClearSelection,
        onClick = {
            if (!selected) {
                id?.let(onClick)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun AnnouncementListItemContent(
    text: String,
    materials: List<Material>,
    creationTime: String,
    updateTime: String,
    creator: User?,
    itemUserRole: UserRole,
    currentUserRole: CourseUserRole,
    isCurrentUserCreator: Boolean,
    selected: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onClearSelection: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val systemTimeZone = TimeZone.currentSystemDefault()
    val creationDateTime =
        remember {
            try {
                Instant.parse(creationTime).toLocalDateTime(systemTimeZone)
            } catch (_: Exception) {
                null
            }
        }
    val updateDateTime =
        remember(updateTime) {
            try {
                Instant.parse(updateTime).toLocalDateTime(systemTimeZone)
            } catch (_: Exception) {
                null
            }
        }
    var colors = CardDefaults.outlinedCardColors()
    var elevation = CardDefaults.outlinedCardElevation()
    var border = CardDefaults.outlinedCardBorder()

    if (selected) {
        colors = CardDefaults.elevatedCardColors()
        elevation = CardDefaults.elevatedCardElevation()
        border = CardDefaults.outlinedCardBorder(false)
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = creator?.name.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            supportingContent = {
                if (creationDateTime != null) {
                    val formattedDateTime =
                        formatDate(
                            context = context,
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
                    fullName = creator?.name.orEmpty(),
                    photoUrl = creator?.avatarUrl,
                )
            },
            trailingContent = {
                if (selected) {
                    IconButton(onClick = onClearSelection) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    }
                } else {
                    MenuButton(
                        itemUserRole = itemUserRole,
                        currentUserRole = currentUserRole,
                        isCurrentUserCreator = isCurrentUserCreator,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        onCopyLinkClick = onCopyLinkClick,
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
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
                                AssistChip(
                                    onClick = {
                                        val browserIntent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(material.driveFile.alternateLink),
                                            )
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
                                            imageVector = Icons.AutoMirrored.Filled.InsertDriveFile, // TODO("Add icons based on file type")
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
    itemUserRole: UserRole,
    currentUserRole: CourseUserRole,
    isCurrentUserCreator: Boolean,
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
            when (currentUserRole) {
                CourseUserRole.Student -> {
                    if (isCurrentUserCreator) {
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.delete))
                            },
                            onClick = {
                                expanded = false
                                onDeleteClick()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.copy_link))
                        },
                        onClick = {
                            expanded = false
                            onCopyLinkClick()
                        },
                    )
                }

                is CourseUserRole.Teacher -> {
                    if (itemUserRole == UserRole.TEACHER) {
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = R.string.edit))
                            },
                            onClick = {
                                expanded = false
                                onEditClick()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.delete))
                        },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.copy_link))
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

@Preview
@Composable
private fun AnnouncementListItemPreview() {
    EdumateTheme {
        AnnouncementListItemContent(
            text = "Text",
            materials = emptyList(),
            creationTime = "2025-01-28T08:26:42.830742+00:00",
            updateTime = "2025-01-28T08:26:42.830742+00:00",
            creator = User(name = "User"),
            itemUserRole = UserRole.TEACHER,
            currentUserRole = CourseUserRole.Teacher(isCourseOwner = true),
            isCurrentUserCreator = true,
            selected = false,
            onEditClick = {},
            onDeleteClick = {},
            onCopyLinkClick = {},
            onClearSelection = {},
            onClick = {},
        )
    }
}

private fun formatDate(
    context: Context,
    creationDateTime: LocalDateTime,
    updateDateTime: LocalDateTime?,
): String {
    val posted = formatDate(context, creationDateTime)
    val edited = updateDateTime?.let { formatDate(context, it) }

    return if (updateDateTime != null && creationDateTime.compareTo(updateDateTime) == 0) {
        posted
    } else {
        context.getString(R.string.posted_edited_, posted, edited.toString())
    }
}

private fun formatDate(
    context: Context,
    dateTime: LocalDateTime,
): String {
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
            context.getString(R.string.yesterday)
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
