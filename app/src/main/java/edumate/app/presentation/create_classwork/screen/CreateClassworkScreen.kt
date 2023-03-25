package edumate.app.presentation.create_classwork.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.presentation.classwork.ClassworkType
import edumate.app.presentation.create_classwork.CreateClassworkUiEvent
import edumate.app.presentation.create_classwork.CreateClassworkUiState
import edumate.app.presentation.create_classwork.CreateClassworkViewModel
import edumate.app.presentation.create_classwork.screen.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateClassworkScreen(
    viewModel: CreateClassworkViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    workType: String
) {
    val uiState = viewModel.uiState
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onEvent(CreateClassworkUiEvent.OnGetContent(it))
            }
        }

    when (workType) {
        "${ClassworkType.ASSIGNMENT}" -> {
            ContentAssignment(
                courseTitle = "Name",
                uiState = uiState,
                onTitleChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                },
                onDescriptionChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                },
                onDueDateChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnDueDateChange(it))
                },
                onOpenAttachmentMenuChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(it))
                },
                onOpenDatePickerDialogChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(it))
                },
                onOpenPointsDialogChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(it))
                },
                onOpenTimePickerDialogChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(it))
                },
                onPointsChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnPointsChange(it))
                }
            )
        }
        "${ClassworkType.QUESTION}" -> {
            ContentQuestion(
                courseTitle = "Name",
                uiState = uiState,
                onTitleChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                },
                onDescriptionChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                },
                onDueDateChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnDueDateChange(it))
                },
                onOpenAttachmentMenuChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(it))
                },
                onOpenDatePickerDialogChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(it))
                },
                onOpenPointsDialogChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(it))
                },
                onOpenTimePickerDialogChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(it))
                },
                onPointsChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnPointsChange(it))
                }
            )
        }
        "${ClassworkType.MATERIAL}" -> {
            ContentMaterial(
                courseTitle = "Title",
                uiState = uiState,
                onTitleChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                },
                onDescriptionChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                },
                onOpenAttachmentMenuChange = {
                    viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(it))
                }
            )
        }
    }

    AttachmentMenuBottomSheet(
        openBottomSheet = uiState.openAttachmentMenu,
        onInsertLinkClick = {
            viewModel.onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = { filePicker.launch("*/*") },
        onDismissRequest = {
            viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(false))
        }
    )

    AddLinkDialog(
        openDialog = uiState.openAddLinkDialog,
        onConfirm = { /* TODO() */ },
        onDismissRequest = {
            viewModel.onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(false))
        }
    )
}

@Composable
private fun ContentAssignment(
    courseTitle: String,
    uiState: CreateClassworkUiState,
    onTitleChange: (title: String) -> Unit,
    onDescriptionChange: (description: String) -> Unit,
    onDueDateChange: (dueDate: Date?) -> Unit,
    onOpenAttachmentMenuChange: (open: Boolean) -> Unit,
    onOpenDatePickerDialogChange: (open: Boolean) -> Unit,
    onOpenPointsDialogChange: (open: Boolean) -> Unit,
    onOpenTimePickerDialogChange: (open: Boolean) -> Unit,
    onPointsChange: (points: String?) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

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
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.assignment_title))
                        },
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
                        uiState.attachments.onEach {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = it,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
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
            onClick = {},
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentQuestion(
    courseTitle: String,
    uiState: CreateClassworkUiState,
    onTitleChange: (title: String) -> Unit,
    onDescriptionChange: (description: String) -> Unit,
    onDueDateChange: (dueDate: Date?) -> Unit,
    onOpenAttachmentMenuChange: (open: Boolean) -> Unit,
    onOpenDatePickerDialogChange: (open: Boolean) -> Unit,
    onOpenPointsDialogChange: (open: Boolean) -> Unit,
    onOpenTimePickerDialogChange: (open: Boolean) -> Unit,
    onPointsChange: (points: String?) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val options = listOf("Short answer", "Multiple choice")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

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
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.question_title))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true
                        )
                    )
                },
                leadingIcon = Icons.Default.LiveHelp
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
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOptionText,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                },
                leadingIcon = Icons.Default.Quiz
            )
            if (selectedOptionText == options[1]) {
                FieldListItem(
                    title = "Add answers",
                    onClick = {}
                )
            }
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
                        uiState.attachments.onEach {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = it,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
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
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(id = Strings.ask))
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

@Composable
private fun ContentMaterial(
    courseTitle: String,
    uiState: CreateClassworkUiState,
    onTitleChange: (title: String) -> Unit,
    onDescriptionChange: (description: String) -> Unit,
    onOpenAttachmentMenuChange: (open: Boolean) -> Unit
) {
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
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.material_title))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true
                        )
                    )
                },
                leadingIcon = Icons.Default.Book,
                trailingContent = {}
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
                leadingIcon = Icons.Default.People,
                trailingContent = {}
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
                leadingIcon = Icons.Default.Description,
                trailingContent = {}
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
                        uiState.attachments.onEach {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = it,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null
                                    )
                                },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
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
                leadingIcon = Icons.Default.Attachment,
                trailingContent = {}
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = stringResource(id = Strings.post))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}