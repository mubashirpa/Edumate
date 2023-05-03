package edumate.app.presentation.create_classwork.screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.R.array as Arrays
import edumate.app.R.string as Strings
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.create_classwork.CreateClassworkUiEvent
import edumate.app.presentation.create_classwork.CreateClassworkUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentQuestion(
    courseTitle: String,
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val fileUtils = remember {
        FileUtils(context)
    }
    val options = stringArrayResource(id = Arrays.question_type)
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

    Column(modifier = Modifier.fillMaxSize()) {
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
                        onValueChange = {
                            onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.question_title))
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
                        onValueChange = {
                            onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                        },
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
                            options.forEachIndexed { index, selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        val questionType = when (index) {
                                            0 -> CourseWorkType.SHORT_ANSWER_QUESTION
                                            else -> CourseWorkType.MULTIPLE_CHOICE_QUESTION
                                        }
                                        onEvent(
                                            CreateClassworkUiEvent.OnWorkTypeChange(questionType)
                                        )
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
            AnimatedVisibility(
                visible = uiState.workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION
            ) {
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
                            AnswersListItem(uiState.choices)
                        }
                    }
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
                        uiState.attachments.onEachIndexed { index, material ->
                            ListItem(
                                headlineContent = {
                                    val title: String = if (material.driveFile != null) {
                                        material.driveFile.title ?: material.driveFile.url
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
                                    if (material.link?.thumbnailUrl.isNullOrEmpty()) {
                                        val icon = if (material.driveFile != null) {
                                            when (fileUtils.getFileType(material.driveFile.type)) {
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
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null
                                        )
                                    } else {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(material.link?.thumbnailUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Inside,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingContent = {
                                    IconButton(onClick = {
                                        onEvent(
                                            CreateClassworkUiEvent.OnRemoveAttachment(
                                                index
                                            )
                                        )
                                    }) {
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
                            modifier = Modifier.clickable {
                                onEvent(
                                    CreateClassworkUiEvent.OnOpenAttachmentMenuChange(
                                        true
                                    )
                                )
                            }
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
                        IconButton(
                            onClick = { onEvent(CreateClassworkUiEvent.OnPointsChange(null)) }
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                } else {
                    null
                },
                onClick = {
                    onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(true))
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
                                onClick = {
                                    onEvent(
                                        CreateClassworkUiEvent.OnOpenDatePickerDialogChange(
                                            true
                                        )
                                    )
                                }
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
                                    onClick = {
                                        onEvent(
                                            CreateClassworkUiEvent.OnOpenDatePickerDialogChange(
                                                true
                                            )
                                        )
                                    }
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
                                    .clickable {
                                        onEvent(
                                            CreateClassworkUiEvent.OnOpenTimePickerDialogChange(
                                                true
                                            )
                                        )
                                    },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                leadingIcon = Icons.Default.CalendarToday,
                trailingContent = if (uiState.dueDate != null) {
                    {
                        IconButton(
                            onClick = { onEvent(CreateClassworkUiEvent.OnDueDateChange(null)) }
                        ) {
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
            onClick = { onEvent(CreateClassworkUiEvent.CreateClasswork) },
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
        onConfirm = { onEvent(CreateClassworkUiEvent.OnDueDateChange(it)) },
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(false))
        }
    )

    ContentTimePickerDialog(
        date = uiState.dueDate,
        openDialog = uiState.openTimePickerDialog,
        onConfirm = { onEvent(CreateClassworkUiEvent.OnDueDateChange(it)) },
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(false))
        }
    )

    PointsDialog(
        openDialog = uiState.openPointsDialog,
        currentPoint = uiState.points,
        onConfirmClick = { onEvent(CreateClassworkUiEvent.OnPointsChange(it)) },
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(false))
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AnswersListItem(
    choices: SnapshotStateList<String>
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        choices.onEachIndexed { index, s ->
            ListItem(
                headlineContent = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = null
                        )
                        BasicTextField(
                            value = s,
                            onValueChange = {
                                choices[index] = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }
                },
                trailingContent = {
                    if (choices.size > 1) {
                        IconButton(onClick = { choices.removeAt(index) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
            Divider()
        }
        ListItem(
            headlineContent = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = false,
                        onClick = null
                    )
                    Text(
                        text = stringResource(id = Strings.add_option),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            },
            modifier = Modifier.clickable {
                choices.add("Option ${choices.size + 1}")
            }
        )
    }
}