package edumate.app.presentation.createClasswork.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.LiveHelp
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.presentation.components.FieldListItem
import edumate.app.presentation.createClasswork.CreateClassworkUiEvent
import edumate.app.presentation.createClasswork.CreateClassworkUiState
import java.text.SimpleDateFormat
import java.util.Locale
import edumate.app.R.array as Arrays
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentQuestion(
    courseTitle: String,
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit,
) {
    val context = LocalContext.current
    val dateFormatter =
        remember {
            SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        }
    val timeFormatter =
        remember {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }
    val fileUtils =
        remember {
            FileUtils(context)
        }
    val questionTypes = stringArrayResource(id = Arrays.question_type)
    val focusRequester =
        remember {
            FocusRequester()
        }
    val isTitleError = uiState.titleError != null

    LaunchedEffect(questionTypes) {
        onEvent(CreateClassworkUiEvent.OnQuestionTypeSelectionOptionValueChange(questionTypes[0]))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            FieldListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = {
                            onEvent(CreateClassworkUiEvent.OnTitleValueChange(it))
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                        label = {
                            Text(text = stringResource(id = Strings.question_title))
                        },
                        supportingText =
                            if (isTitleError) {
                                {
                                    Text(text = uiState.titleError!!.asString())
                                }
                            } else {
                                null
                            },
                        isError = isTitleError,
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                            ),
                    )
                },
                leadingIcon = Icons.AutoMirrored.Filled.LiveHelp,
            )
            FieldListItem(
                headlineContent = {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.extraSmall,
                                )
                                .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = courseTitle)
                            },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = stringResource(id = Strings.all_students))
                            },
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                },
                leadingIcon = Icons.Default.People,
            )
            FieldListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = {
                            onEvent(CreateClassworkUiEvent.OnDescriptionValueChange(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.description))
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                            ),
                    )
                },
                leadingIcon = Icons.Default.Description,
            )
            FieldListItem(
                headlineContent = {
                    ExposedDropdownMenuBox(
                        expanded = uiState.questionTypeDropdownExpanded,
                        onExpandedChange = {
                            onEvent(CreateClassworkUiEvent.OnQuestionTypeDropdownExpandedChange(it))
                        },
                    ) {
                        OutlinedTextField(
                            value = uiState.questionTypeSelectionOption,
                            onValueChange = {},
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.questionTypeDropdownExpanded)
                            },
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.questionTypeDropdownExpanded,
                            onDismissRequest = {
                                onEvent(
                                    CreateClassworkUiEvent.OnQuestionTypeDropdownExpandedChange(
                                        false,
                                    ),
                                )
                            },
                        ) {
                            questionTypes.forEachIndexed { index, selectionOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(selectionOption)
                                    },
                                    onClick = {
                                        onEvent(
                                            CreateClassworkUiEvent.OnQuestionTypeSelectionOptionValueChange(
                                                selectionOption,
                                            ),
                                        )
                                        val questionType =
                                            when (index) {
                                                0 -> CourseWorkType.SHORT_ANSWER_QUESTION
                                                else -> CourseWorkType.MULTIPLE_CHOICE_QUESTION
                                            }
                                        onEvent(
                                            CreateClassworkUiEvent.OnWorkTypeValueChange(
                                                questionType,
                                            ),
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                },
                leadingIcon = Icons.Default.Quiz,
            )
            AnimatedVisibility(
                visible = uiState.workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION,
            ) {
                FieldListItem(
                    headlineContent = {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = MaterialTheme.shapes.extraSmall,
                                    ),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            MultipleChoiceListItem(uiState.choices)
                        }
                    },
                )
            }
            FieldListItem(
                headlineContent = {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.extraSmall,
                                ),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        uiState.attachments.onEachIndexed { index, material ->
                            ListItem(
                                headlineContent = {
                                    val title: String =
                                        if (material.driveFile != null) {
                                            material.driveFile.title.orEmpty()
                                        } else {
                                            material.link?.title.orEmpty()
                                        }
                                    Text(
                                        text = title,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                    )
                                },
                                leadingContent = {
                                    if (material.link?.thumbnailUrl.isNullOrEmpty()) {
                                        val icon =
                                            if (material.driveFile != null) {
                                                val mimeType =
                                                    fileUtils.getMimeType(Uri.parse(material.driveFile.alternateLink.orEmpty()))
                                                when (fileUtils.getFileType(mimeType)) {
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
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                        )
                                    } else {
                                        AsyncImage(
                                            model =
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(material.link?.thumbnailUrl)
                                                    .crossfade(true)
                                                    .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Inside,
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            onEvent(CreateClassworkUiEvent.OnRemoveAttachment(index))
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null,
                                        )
                                    }
                                },
                            )
                            HorizontalDivider()
                        }
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(id = Strings.add_attachment),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                            modifier =
                                Modifier.clickable {
                                    onEvent(
                                        CreateClassworkUiEvent.OnShowAddAttachmentBottomSheetChange(
                                            true,
                                        ),
                                    )
                                },
                        )
                    }
                },
                leadingIcon = Icons.Default.Attachment,
            )
            FieldListItem(
                title =
                    if (uiState.points != null && uiState.points != "0") {
                        stringResource(id = Strings._points, uiState.points)
                    } else {
                        stringResource(id = Strings.unmarked)
                    },
                leadingIcon = Icons.AutoMirrored.Filled.PlaylistAddCheck,
                trailingContent =
                    if (uiState.points != null && uiState.points != "0") {
                        {
                            IconButton(
                                onClick = {
                                    onEvent(CreateClassworkUiEvent.OnPointsValueChange(null))
                                },
                            ) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    } else {
                        null
                    },
                onClick = {
                    onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(true))
                },
            )
            FieldListItem(
                headlineContent = {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.extraSmall,
                                )
                                .clip(MaterialTheme.shapes.extraSmall)
                                .clickable(
                                    enabled = uiState.dueDate == null,
                                    onClick = {
                                        onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(true))
                                    },
                                ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text =
                                if (uiState.dueDate != null) {
                                    dateFormatter.format(uiState.dueDate)
                                } else {
                                    stringResource(id = Strings.due_date)
                                },
                            modifier =
                                Modifier
                                    .padding(start = 16.dp)
                                    .clickable(
                                        enabled = uiState.dueDate != null,
                                        onClick = {
                                            onEvent(
                                                CreateClassworkUiEvent.OnOpenDatePickerDialogChange(
                                                    true,
                                                ),
                                            )
                                        },
                                    ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if (uiState.dueDate != null) {
                            Text(
                                text = timeFormatter.format(uiState.dueDate),
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .clickable {
                                            onEvent(
                                                CreateClassworkUiEvent.OnOpenTimePickerDialogChange(
                                                    true,
                                                ),
                                            )
                                        },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                },
                leadingIcon = Icons.Default.CalendarToday,
                trailingContent =
                    if (uiState.dueDate != null) {
                        {
                            IconButton(
                                onClick = {
                                    onEvent(CreateClassworkUiEvent.OnDueDateValueChange(null))
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                )
                            }
                        }
                    } else {
                        null
                    },
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                onEvent(CreateClassworkUiEvent.CreateCourseWork)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            Text(text = stringResource(id = Strings.ask))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

    DatePickerDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(false))
        },
        calendar = uiState.dueDate,
        openDialog = uiState.openDatePickerDialog,
        onConfirmClick = {
            onEvent(CreateClassworkUiEvent.OnDueDateValueChange(it))
        },
    )

    TimePickerDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(false))
        },
        openDialog = uiState.openTimePickerDialog,
        onConfirmClick = {
            onEvent(CreateClassworkUiEvent.OnDueDateValueChange(it))
        },
    )

    PointsDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(false))
        },
        openDialog = uiState.openPointsDialog,
        currentPoint = uiState.points,
        onConfirmClick = {
            onEvent(CreateClassworkUiEvent.OnPointsValueChange(it))
        },
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
