package edumate.app.presentation.stream.screen.components

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Attachment
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
import edumate.app.domain.model.announcements.Announcement
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.UserAvatar
import edumate.app.R.string as Strings

@Composable
fun AnnouncementsListItem(
    announcement: Announcement,
    modifier: Modifier = Modifier,
    currentUserId: String,
    currentUserType: UserType,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
    val isCreator = announcement.creatorUserId == currentUserId

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(width = Dp.Hairline, color = MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .padding(start = 16.dp, end = 8.dp)
                    .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // leadingContent
            UserAvatar(
                id = announcement.creatorUserId,
                fullName =
                    announcement.creatorProfile?.displayName
                        ?: announcement.creatorProfile?.emailAddress.orEmpty(),
                photoUrl = announcement.creatorProfile?.photoUrl,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                // headlineContent
                Text(
                    text = announcement.creatorProfile?.displayName.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                )
                // supportingContent
                val creationTime = announcement.creationTime
                val updateTime = announcement.updateTime
                if (creationTime != null) {
                    val date = DateUtils.getRelativeTimeSpanString(creationTime)
                    val text =
                        if (updateTime != null && updateTime != creationTime) {
                            val edited = DateUtils.getRelativeTimeSpanString(creationTime)
                            stringResource(id = Strings._edited_, date, edited)
                        } else {
                            "$date"
                        }
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            // trailingContent
            if (isCreator || currentUserType == UserType.TEACHER) {
                Spacer(modifier = Modifier.width(16.dp))
                MenuButton(
                    isCreator = isCreator,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
        ) {
            Text(
                text = announcement.text,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            if (announcement.materials.isNotEmpty()) {
                Row(
                    modifier =
                        Modifier
                            .padding(top = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    announcement.materials.forEachIndexed { index, material ->
                        val icon =
                            if (material.driveFile != null) {
                                when (fileUtils.getFileType(material.driveFile.type)) {
                                    FileType.IMAGE -> Icons.Default.Image
                                    FileType.VIDEO -> Icons.Default.VideoFile
                                    FileType.AUDIO -> Icons.Default.AudioFile
                                    FileType.PDF -> Icons.Default.PictureAsPdf
                                    FileType.UNKNOWN -> Icons.AutoMirrored.Filled.InsertDriveFile
                                }
                            } else if (material.link != null) {
                                Icons.Default.Link
                            } else {
                                Icons.Default.Attachment
                            }

                        when {
                            material.driveFile != null -> {
                                AssistChip(
                                    onClick = {
                                        val browserIntent =
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(material.driveFile.url),
                                            )
                                        context.startActivity(browserIntent)
                                    },
                                    label = {
                                        Text(
                                            text =
                                                material.driveFile.title
                                                    ?: material.driveFile.url,
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
                                            text = material.link.title ?: material.link.url,
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
                        }
                        if (index != announcement.materials.lastIndex) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MenuButton(
    isCreator: Boolean,
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
            if (isCreator) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.edit)) },
                    onClick = {
                        expanded = false
                        onEditClick()
                    },
                )
            }
            DropdownMenuItem(
                text = { Text(text = stringResource(id = Strings.delete)) },
                onClick = {
                    expanded = false
                    onDeleteClick()
                },
            )
        }
    }
}
