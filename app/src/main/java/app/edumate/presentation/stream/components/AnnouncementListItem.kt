package app.edumate.presentation.stream.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.edumate.R
import app.edumate.core.utils.DateTimeUtils
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
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
    currentUserRole: CourseUserRole,
    currentUserId: String,
    selected: Boolean,
    fileUtils: FileUtils,
    onPinClick: (id: String) -> Unit,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: (id: String) -> Unit,
    onClearSelection: () -> Unit,
    onFileAttachmentClick: (mimeType: FileType, url: String, title: String?) -> Unit,
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
        totalComments = announcement.totalComments ?: 0,
        pinned = announcement.pinned == true,
        currentUserRole = currentUserRole,
        isCurrentUserCreator = isCurrentUserCreator,
        selected = selected,
        fileUtils = fileUtils,
        onPinClick = {
            id?.let(onPinClick)
        },
        onEditClick = {
            id?.let(onEditClick)
        },
        onDeleteClick = {
            id?.let(onDeleteClick)
        },
        onClearSelection = onClearSelection,
        onFileAttachmentClick = onFileAttachmentClick,
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
    totalComments: Int,
    pinned: Boolean,
    currentUserRole: CourseUserRole,
    isCurrentUserCreator: Boolean,
    selected: Boolean,
    fileUtils: FileUtils,
    onPinClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClearSelection: () -> Unit,
    onFileAttachmentClick: (mimeType: FileType, url: String, title: String?) -> Unit,
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
    val showMenuButton =
        when (currentUserRole) {
            CourseUserRole.Student -> isCurrentUserCreator
            is CourseUserRole.Teacher -> true
        }

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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = creator?.name.orEmpty(),
                        modifier = Modifier.weight(1f, false),
                        color = MaterialTheme.colorScheme.onSurface,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    if (pinned) {
                        Text(
                            text = stringResource(R.string.pinned),
                            modifier =
                                Modifier
                                    .clip(MaterialTheme.shapes.large)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
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
                    photoUrl = creator?.photoUrl,
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
                } else if (showMenuButton) {
                    MenuButton(
                        pinned = pinned,
                        itemUserRole = creator?.role,
                        currentUserRole = currentUserRole,
                        isCurrentUserCreator = isCurrentUserCreator,
                        onPinClick = onPinClick,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
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
                            .padding(top = 12.dp)
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    materials.forEach { material ->
                        AttachmentsListItem(
                            material = material,
                            fileUtils = fileUtils,
                            onClickFile = onFileAttachmentClick,
                            onClickLink = { url ->
                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(browserIntent)
                            },
                            modifier = Modifier.widthIn(max = 180.dp),
                        )
                    }
                }
            }
            if (totalComments > 0) {
                Text(
                    text =
                        pluralStringResource(
                            R.plurals.replies,
                            totalComments,
                            totalComments,
                        ),
                    modifier =
                        Modifier
                            .padding(top = 12.dp)
                            .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun MenuButton(
    pinned: Boolean,
    itemUserRole: UserRole?,
    currentUserRole: CourseUserRole,
    isCurrentUserCreator: Boolean,
    onPinClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
                }

                is CourseUserRole.Teacher -> {
                    DropdownMenuItem(
                        text = {
                            val text =
                                if (pinned) {
                                    stringResource(R.string.unpin)
                                } else {
                                    stringResource(R.string.pin)
                                }
                            Text(text = text)
                        },
                        onClick = {
                            expanded = false
                            onPinClick()
                        },
                    )
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
            totalComments = 10,
            pinned = true,
            currentUserRole = CourseUserRole.Teacher(isCourseOwner = true),
            isCurrentUserCreator = true,
            selected = false,
            fileUtils = FileUtils(LocalContext.current),
            onPinClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onClearSelection = {},
            onFileAttachmentClick = { _, _, _ -> },
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
