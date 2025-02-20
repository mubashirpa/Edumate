package app.edumate.presentation.createCourseWork.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Subject
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedSuggestionChip
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.utils.FileUtils
import app.edumate.presentation.components.FieldListItem
import app.edumate.presentation.createCourseWork.CreateCourseWorkUiEvent
import app.edumate.presentation.createCourseWork.CreateCourseWorkUiState
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

@Composable
fun ContentAssignment(
    uiState: CreateCourseWorkUiState,
    onEvent: (CreateCourseWorkUiEvent) -> Unit,
    courseName: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val fileUtils = remember { FileUtils(context) }
    val itemModifier =
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraSmall,
            )

    Column(modifier = modifier) {
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
                        state = uiState.title,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                        label = {
                            Text(text = stringResource(id = R.string.assignment_title))
                        },
                        supportingText =
                            uiState.titleError?.let { error ->
                                {
                                    Text(text = error.asString())
                                }
                            },
                        isError = uiState.titleError != null,
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrectEnabled = true,
                            ),
                    )
                },
                leadingIcon = Icons.AutoMirrored.Outlined.Assignment,
            )
            FieldListItem(
                headlineContent = {
                    Row(
                        modifier = itemModifier.horizontalScroll(rememberScrollState()),
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
                                Text(text = stringResource(id = R.string.all_students))
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
                        state = uiState.description,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = R.string.description))
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrectEnabled = true,
                            ),
                    )
                },
                leadingIcon = Icons.AutoMirrored.Outlined.Subject,
            )
            FieldListItem(
                headlineContent = {
                    Column(
                        modifier = itemModifier,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        uiState.attachments.onEachIndexed { index, material ->
                            AttachmentsListItem(
                                material = material,
                                fileUtils = fileUtils,
                                onRemoveAttachmentClick = {
                                    onEvent(CreateCourseWorkUiEvent.RemoveAttachment(index))
                                },
                            )
                            HorizontalDivider()
                        }
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(id = R.string.add_attachment),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                            modifier =
                                Modifier.clickable {
                                    onEvent(
                                        CreateCourseWorkUiEvent.OnShowAddAttachmentBottomSheetChange(
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
                    if (uiState.points != null && uiState.points > 0) {
                        pluralStringResource(R.plurals.points, uiState.points, uiState.points)
                    } else {
                        stringResource(id = R.string.set_total_points)
                    },
                leadingIcon = Icons.Outlined.InsertChart,
                trailingContent =
                    if (uiState.points != null && uiState.points > 0) {
                        {
                            IconButton(
                                onClick = {
                                    onEvent(CreateCourseWorkUiEvent.OnPointsValueChange(null))
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
                    onEvent(CreateCourseWorkUiEvent.OnOpenPointsDialogChange(true))
                },
            )
            FieldListItem(
                headlineContent = {
                    val interactionSource = remember { MutableInteractionSource() }
                    Row(
                        modifier =
                            itemModifier
                                .indication(interactionSource, ripple())
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    enabled = uiState.dueTime == null,
                                    onClick = {
                                        onEvent(
                                            CreateCourseWorkUiEvent.OnOpenDatePickerDialogChange(
                                                true,
                                            ),
                                        )
                                    },
                                ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (uiState.dueTime != null) {
                            Text(
                                text =
                                    uiState.dueTime.format(
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
                                                    CreateCourseWorkUiEvent.OnOpenDatePickerDialogChange(
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
                                    uiState.dueTime.format(
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
                                        .padding(horizontal = 16.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null,
                                            onClick = {
                                                onEvent(
                                                    CreateCourseWorkUiEvent.OnOpenTimePickerDialogChange(
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
                                text = stringResource(id = R.string.set_due_date),
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                },
                leadingIcon = Icons.Outlined.CalendarToday,
                trailingContent =
                    if (uiState.dueTime != null) {
                        {
                            IconButton(
                                onClick = {
                                    onEvent(CreateCourseWorkUiEvent.OnDueTimeValueChange(null))
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
                onEvent(CreateCourseWorkUiEvent.CreateCourseWork)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            val text =
                if (uiState.isNewCourseWork) {
                    stringResource(id = R.string.assign)
                } else {
                    stringResource(id = R.string.save)
                }
            Text(text = text)
        }
        Spacer(modifier = Modifier.height(12.dp))

        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }
    }

    DatePickerDialog(
        onDismissRequest = {
            onEvent(CreateCourseWorkUiEvent.OnOpenDatePickerDialogChange(false))
        },
        open = uiState.openDatePickerDialog,
        dateTime = uiState.dueTime,
        onConfirmButtonClick = {
            onEvent(CreateCourseWorkUiEvent.OnDueTimeValueChange(it))
        },
    )

    TimePickerDialog(
        onDismissRequest = {
            onEvent(CreateCourseWorkUiEvent.OnOpenTimePickerDialogChange(false))
        },
        open = uiState.openTimePickerDialog,
        dateTime = uiState.dueTime,
        onConfirmButtonClick = {
            onEvent(CreateCourseWorkUiEvent.OnDueTimeValueChange(it))
        },
    )

    PointsDialog(
        onDismissRequest = {
            onEvent(CreateCourseWorkUiEvent.OnOpenPointsDialogChange(false))
        },
        open = uiState.openPointsDialog,
        currentPoint = uiState.points?.toString(),
        onConfirmButtonClick = {
            onEvent(CreateCourseWorkUiEvent.OnPointsValueChange(it))
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ContentAssignmentPreview() {
    EdumateTheme {
        ContentAssignment(
            uiState = CreateCourseWorkUiState(),
            onEvent = {},
            courseName = "Course",
        )
    }
}
