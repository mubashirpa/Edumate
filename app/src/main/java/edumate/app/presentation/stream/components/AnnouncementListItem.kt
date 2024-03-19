package edumate.app.presentation.stream.components

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.classroom.announcements.Announcement
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.presentation.components.UserAvatar
import kotlinx.datetime.Instant
import edumate.app.R.string as Strings

@Composable
fun AnnouncementListItem(
    announcement: Announcement,
    modifier: Modifier = Modifier,
    course: Course,
    userId: String,
    onClick: (id: String) -> Unit,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: (id: String) -> Unit,
    onCopyLinkClick: (link: String) -> Unit,
) {
    val context = LocalContext.current
    val isCreator = announcement.creatorUserId == userId
    val id = announcement.id
    val fileUtils =
        remember {
            FileUtils(context)
        }
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

    OutlinedCard(
        onClick = {
            id?.let(onClick)
        },
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
                    text = announcement.creator?.name?.fullName.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            supportingContent = {
                val creationDateTime =
                    remember {
                        try {
                            Instant.parse(announcement.creationTime.orEmpty())
                        } catch (e: Exception) {
                            null
                        }
                    }
                val updateDateTime =
                    remember {
                        try {
                            Instant.parse(announcement.updateTime.orEmpty())
                        } catch (e: Exception) {
                            null
                        }
                    }

                if (creationDateTime != null) {
                    val posted =
                        DateUtils.getRelativeTimeSpanString(creationDateTime.toEpochMilliseconds())
                    val text =
                        if (updateDateTime != null && updateDateTime != creationDateTime) {
                            val edited =
                                DateUtils.getRelativeTimeSpanString(updateDateTime.toEpochMilliseconds())

                            stringResource(id = Strings._edited_, posted, edited)
                        } else {
                            "$posted"
                        }

                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            },
            leadingContent = {
                UserAvatar(
                    id = announcement.creator?.id.orEmpty(),
                    fullName = announcement.creator?.name?.fullName.orEmpty(),
                    photoUrl = announcement.creator?.photoUrl,
                )
            },
            trailingContent = {
                MenuButton(
                    userRole = userRole,
                    targetUserRole = targetUserRole,
                    isCreator = isCreator,
                    onEditClick = {
                        id?.let(onEditClick)
                    },
                    onDeleteClick = {
                        id?.let(onDeleteClick)
                    },
                    onCopyLinkClick = {
                        announcement.alternateLink?.let(onCopyLinkClick)
                    },
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
                text = announcement.text.orEmpty(),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            if (!announcement.materials.isNullOrEmpty()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    announcement.materials.forEach { material ->
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

private enum class UserRole {
    TEACHER,
    STUDENT,
}
