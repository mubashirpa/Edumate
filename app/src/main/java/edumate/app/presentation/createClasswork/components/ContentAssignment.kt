package edumate.app.presentation.createClasswork.components

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.presentation.components.FieldListItem
import edumate.app.presentation.createClasswork.CreateClassworkUiEvent
import edumate.app.presentation.createClasswork.CreateClassworkUiState
import edumate.app.presentation.createClasswork.LoremIpsumSingleWord
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import edumate.app.R.string as Strings

@Composable
fun ContentAssignment(
    className: String,
    classworkId: String?,
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit,
) {
    val context = LocalContext.current
    val fileUtils =
        remember {
            FileUtils(context)
        }
    val focusRequester =
        remember {
            FocusRequester()
        }
    val isTitleError = uiState.titleError != null

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp),
    ) {
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
                            Text(text = stringResource(id = Strings.assignment_title))
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
                leadingIcon = Icons.AutoMirrored.Filled.Assignment,
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
                                Text(text = className)
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
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                )
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
                                text = stringResource(id = Strings.due_date),
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                },
                leadingIcon = Icons.Default.CalendarToday,
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
                        stringResource(id = Strings.assign)
                    },
            )
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

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentAssignmentPreview(
    @PreviewParameter(LoremIpsumSingleWord::class) className: String,
) {
    EdumateTheme(dynamicColor = false) {
        ContentAssignment(
            className = className,
            classworkId = null,
            uiState = CreateClassworkUiState(),
            onEvent = {},
        )
    }
}
