package edumate.app.presentation.create_classwork.screen.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.R.string as Strings
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.presentation.create_classwork.CreateClassworkUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ContentAssignment(
    courseTitle: String,
    uiState: CreateClassworkUiState,
    onTitleChange: (title: String) -> Unit,
    onDescriptionChange: (description: String) -> Unit,
    onDueDateChange: (dueDate: Date?) -> Unit,
    onOpenAttachmentMenuChange: (open: Boolean) -> Unit,
    onOpenDatePickerDialogChange: (open: Boolean) -> Unit,
    onOpenPointsDialogChange: (open: Boolean) -> Unit,
    onOpenTimePickerDialogChange: (open: Boolean) -> Unit,
    onPointsChange: (points: String?) -> Unit,
    onRemoveAttachment: (position: Int) -> Unit,
    onAssignAssignment: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val fileUtils = remember {
        FileUtils(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            FieldListItem(
                headlineContent = {
                    @Suppress("SENSELESS_COMPARISON")
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.assignment_title))
                        },
                        supportingText = if (uiState.titleError != null) {
                            { Text(text = uiState.titleError.asString()) }
                        } else {
                            null
                        },
                        isError = uiState.titleError != null,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true
                        )
                    )
                },
                leadingIcon = Icons.Default.Assignment
            )
            FieldListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = courseTitle) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = stringResource(id = Strings.all_students)) }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                },
                leadingIcon = Icons.Default.People
            )
            FieldListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.description))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true
                        )
                    )
                },
                leadingIcon = Icons.Default.Description
            )
            FieldListItem(
                headlineContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            ),
                        verticalArrangement = Arrangement.Center
                    ) {
                        uiState.attachments.onEachIndexed { index, material ->
                            ListItem(
                                headlineContent = {
                                    val title: String = if (material.file != null) {
                                        material.file.title ?: material.file.url
                                    } else {
                                        material.link?.title ?: material.link?.url.orEmpty()
                                    }
                                    Text(
                                        text = title,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                leadingContent = {
                                    val icon = if (material.file != null) {
                                        when (fileUtils.getFileType(material.file.type)) {
                                            FileType.IMAGE -> Icons.Default.Image
                                            FileType.VIDEO -> Icons.Default.VideoFile
                                            FileType.AUDIO -> Icons.Default.AudioFile
                                            FileType.PDF -> Icons.Default.PictureAsPdf
                                            FileType.UNKNOWN -> Icons.Default.InsertDriveFile
                                        }
                                    } else if (material.link != null) {
                                        Icons.Default.Link
                                    } else {
                                        Icons.Default.Attachment
                                    }
                                    if (material.link?.thumbnailUrl != null && material.link.thumbnailUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(material.link.thumbnailUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Inside,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null
                                        )
                                    }
                                },
                                trailingContent = {
                                    IconButton(onClick = { onRemoveAttachment(index) }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                            Divider()
                        }
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(id = Strings.add_attachment),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            modifier = Modifier.clickable { onOpenAttachmentMenuChange(true) }
                        )
                    }
                },
                leadingIcon = Icons.Default.Attachment
            )
            FieldListItem(
                title = if (uiState.points != null && uiState.points != "0") {
                    stringResource(id = Strings._points, uiState.points)
                } else {
                    stringResource(id = Strings.unmarked)
                },
                leadingIcon = Icons.Default.PlaylistAddCheck,
                trailingContent = if (uiState.points != null && uiState.points != "0") {
                    {
                        IconButton(onClick = { onPointsChange(null) }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                } else {
                    null
                },
                onClick = {
                    onOpenPointsDialogChange(true)
                }
            )
            FieldListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable(
                                enabled = uiState.dueDate == null,
                                onClick = { onOpenDatePickerDialogChange(true) }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.dueDate != null) {
                                dateFormatter.format(uiState.dueDate)
                            } else {
                                stringResource(id = Strings.due_date)
                            },
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable(
                                    enabled = uiState.dueDate != null,
                                    onClick = { onOpenDatePickerDialogChange(true) }
                                ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (uiState.dueDate != null) {
                            Text(
                                text = timeFormatter.format(uiState.dueDate),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable { onOpenTimePickerDialogChange(true) },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                leadingIcon = Icons.Default.CalendarToday,
                trailingContent = if (uiState.dueDate != null) {
                    {
                        IconButton(onClick = { onDueDateChange(null) }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                } else {
                    null
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAssignAssignment,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(id = Strings.assign))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

    ContentDatePickerDialog(
        date = uiState.dueDate,
        openDialog = uiState.openDatePickerDialog,
        onConfirm = onDueDateChange,
        onDismissRequest = {
            onOpenDatePickerDialogChange(false)
        }
    )

    ContentTimePickerDialog(
        date = uiState.dueDate,
        openDialog = uiState.openTimePickerDialog,
        onConfirm = onDueDateChange,
        onDismissRequest = {
            onOpenTimePickerDialogChange(false)
        }
    )

    PointsDialog(
        openDialog = uiState.openPointsDialog,
        currentPoint = uiState.points,
        onConfirmClick = onPointsChange,
        onDismissRequest = {
            onOpenPointsDialogChange(false)
        }
    )
}