package edumate.app.presentation.createClasswork.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.outlined.LiveHelp
import androidx.compose.material.icons.automirrored.outlined.Subject
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QuestionAnswer
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
import androidx.compose.material3.ripple
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.presentation.components.FieldListItem
import edumate.app.presentation.createClasswork.CourseName
import edumate.app.presentation.createClasswork.CreateClassworkUiEvent
import edumate.app.presentation.createClasswork.CreateClassworkUiState
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import edumate.app.R.array as Arrays
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentQuestion(
    courseName: String,
    classworkId: String?,
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit,
) {
    val questionTypes = stringArrayResource(id = Arrays.question_type)
    val focusRequester =
        remember {
            FocusRequester()
        }
    val isTitleError = uiState.titleError != null

    Column {
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
                leadingIcon = Icons.AutoMirrored.Outlined.LiveHelp,
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
                                Text(text = courseName)
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
                leadingIcon = Icons.Outlined.People,
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
                leadingIcon = Icons.AutoMirrored.Outlined.Subject,
            )
            FieldListItem(
                headlineContent = {
                    ExposedDropdownMenuBox(
                        expanded = uiState.questionTypeDropdownExpanded,
                        onExpandedChange = {
                            // Expandable only when creating a question (when classworkId is null)
                            if (classworkId == null) {
                                onEvent(
                                    CreateClassworkUiEvent.OnQuestionTypeDropdownExpandedChange(
                                        it,
                                    ),
                                )
                            }
                        },
                    ) {
                        OutlinedTextField(
                            value =
                                if (uiState.questionTypeSelectionOptionIndex != null) {
                                    questionTypes[uiState.questionTypeSelectionOptionIndex]
                                } else {
                                    stringResource(id = Strings.select_question_type)
                                },
                            onValueChange = {},
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                // Show only when creating a question (when classworkId is null)
                                if (classworkId == null) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.questionTypeDropdownExpanded)
                                }
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
                                            CreateClassworkUiEvent.OnQuestionTypeValueChange(
                                                index,
                                            ),
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                },
                leadingIcon = Icons.Outlined.QuestionAnswer,
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
                                                Icons.AutoMirrored.Filled.InsertDriveFile // TODO("Add icons based on file type")
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
                leadingIcon = Icons.Outlined.Attachment,
            )
            FieldListItem(
                title =
                    if (uiState.points != null && uiState.points != "0") {
                        stringResource(id = Strings._points, uiState.points)
                    } else {
                        stringResource(id = Strings.set_total_points)
                    },
                leadingIcon = Icons.Outlined.InsertChart,
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
                    val interactionSource = remember { MutableInteractionSource() }
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
                                .indication(interactionSource, ripple())
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    enabled = uiState.dueDateTime == null,
                                    onClick = {
                                        onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(true))
                                    },
                                ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (uiState.dueDateTime != null) {
                            Text(
                                text =
                                    uiState.dueDateTime.format(
                                        LocalDateTime.Format {
                                            date(
                                                LocalDate.Format {
                                                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                                                    char(' ')
                                                    dayOfMonth()
                                                    chars(", ")
                                                    year()
                                                },
                                            )
                                        },
                                    ),
                                modifier =
                                    Modifier
                                        .padding(start = 16.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null,
                                            onClick = {
                                                onEvent(
                                                    CreateClassworkUiEvent.OnOpenDatePickerDialogChange(
                                                        true,
                                                    ),
                                                )
                                            },
                                        ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text =
                                    uiState.dueDateTime.format(
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
                                    ),
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp).clickable(
                                            interactionSource = interactionSource,
                                            indication = null,
                                            onClick = {
                                                onEvent(
                                                    CreateClassworkUiEvent.OnOpenTimePickerDialogChange(
                                                        true,
                                                    ),
                                                )
                                            },
                                        ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        } else {
                            Text(
                                text = stringResource(id = Strings.set_due_date),
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                },
                leadingIcon = Icons.Outlined.CalendarToday,
                trailingContent =
                    if (uiState.dueDateTime != null) {
                        {
                            IconButton(
                                onClick = {
                                    onEvent(CreateClassworkUiEvent.OnDueDateTimeValueChange(null))
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
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                onEvent(CreateClassworkUiEvent.CreateCourseWork)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text =
                    if (classworkId != null) {
                        stringResource(id = Strings.save)
                    } else {
                        stringResource(id = Strings.ask)
                    },
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }
    }

    DatePickerDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(false))
        },
        open = uiState.openDatePickerDialog,
        dateTime = uiState.dueDateTime,
        onConfirmButtonClick = {
            onEvent(CreateClassworkUiEvent.OnDueDateTimeValueChange(it))
        },
    )

    TimePickerDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(false))
        },
        open = uiState.openTimePickerDialog,
        dateTime = uiState.dueDateTime,
        onConfirmButtonClick = {
            onEvent(CreateClassworkUiEvent.OnDueDateTimeValueChange(it))
        },
    )

    PointsDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(false))
        },
        open = uiState.openPointsDialog,
        currentPoint = uiState.points,
        onConfirmButtonClick = {
            onEvent(CreateClassworkUiEvent.OnPointsValueChange(it))
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ContentQuestionPreview(
    @PreviewParameter(CourseName::class) courseName: String,
) {
    EdumateTheme {
        ContentQuestion(
            courseName = courseName,
            classworkId = null,
            uiState = CreateClassworkUiState(),
            onEvent = {},
        )
    }
}
