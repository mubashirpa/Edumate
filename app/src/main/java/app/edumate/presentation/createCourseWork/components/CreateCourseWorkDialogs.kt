package app.edumate.presentation.createCourseWork.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.presentation.components.TimePickerDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    open: Boolean,
    dateTime: LocalDateTime?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: (LocalDateTime) -> Unit,
) {
    if (open) {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val initialSelectedDateMillis =
            if (dateTime != null) {
                dateTime.toInstant(systemTimeZone).toEpochMilliseconds()
            } else {
                val currentDateTime = Clock.System.now()
                val tomorrow = currentDateTime.plus(1, DateTimeUnit.DAY, systemTimeZone)
                tomorrow.toEpochMilliseconds()
            }
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = initialSelectedDateMillis,
                selectableDates = TodayAndOnwardsSelectableDates,
            )
        val confirmEnabled =
            remember {
                derivedStateOf { datePickerState.selectedDateMillis != null }
            }
        val configuration = LocalConfiguration.current

        if (configuration.screenHeightDp > 400) {
            datePickerState.displayMode = DisplayMode.Picker
        } else {
            datePickerState.displayMode = DisplayMode.Input
        }

        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                            val selectedDateTime =
                                Instant
                                    .fromEpochMilliseconds(
                                        selectedDateMillis,
                                    ).toLocalDateTime(systemTimeZone)
                            val dueDateTime =
                                LocalDateTime(
                                    year = selectedDateTime.year,
                                    month = selectedDateTime.month,
                                    dayOfMonth = selectedDateTime.dayOfMonth,
                                    hour = 23,
                                    minute = 59,
                                    second = 59,
                                    nanosecond = 999,
                                )
                            onConfirmButtonClick(dueDateTime)
                        }
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = configuration.screenHeightDp > 400,
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsDialog(
    open: Boolean,
    currentPoint: String?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: (points: String?) -> Unit,
) {
    if (open) {
        var point by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(currentPoint ?: "100"))
        }
        val tempPoint: MutableState<String?> = remember { mutableStateOf(currentPoint) }
        val confirmEnabled by remember { derivedStateOf { point.text.isNotBlank() } }

        BasicAlertDialog(onDismissRequest = onDismissRequest) {
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Surface(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text(
                        text = stringResource(id = R.string.change_point_value),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.selectableGroup()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = tempPoint.value != null,
                                    onClick = { tempPoint.value = point.text },
                                    role = Role.RadioButton,
                                ).padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = tempPoint.value != null,
                                onClick = null,
                            )
                            BasicTextField(
                                value = point,
                                onValueChange = {
                                    if (it.text.length <= 3) {
                                        point = it
                                        tempPoint.value = it.text
                                    }
                                },
                                modifier =
                                    Modifier
                                        .padding(start = 16.dp)
                                        .widthIn(max = 500.dp),
                                textStyle =
                                    MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done,
                                    ),
                                keyboardActions =
                                    KeyboardActions(
                                        onDone = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        },
                                    ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            )
                            Text(
                                text = stringResource(id = R.string._points, ""),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = tempPoint.value == null,
                                    onClick = { tempPoint.value = null },
                                    role = Role.RadioButton,
                                ).padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = tempPoint.value == null,
                                onClick = null,
                            )
                            Text(
                                text = stringResource(id = R.string.unmarked),
                                modifier = Modifier.padding(start = 16.dp),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    Row(
                        modifier =
                            Modifier
                                .padding(horizontal = 24.dp)
                                .padding(top = 16.dp)
                                .align(Alignment.End),
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConfirmButtonClick(tempPoint.value?.trim())
                            },
                            enabled = if (tempPoint.value != null) confirmEnabled else true,
                        ) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    open: Boolean,
    dateTime: LocalDateTime?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: (LocalDateTime) -> Unit,
) {
    if (open) {
        val state =
            rememberTimePickerState(
                initialHour = dateTime!!.hour,
                initialMinute = dateTime.minute,
            )
        val showingPicker = remember { mutableStateOf(true) }
        val configuration = LocalConfiguration.current

        TimePickerDialog(
            onDismiss = onDismissRequest,
            onConfirm = {
                onDismissRequest()
                val selectedDateTime =
                    LocalDateTime(
                        year = dateTime.year,
                        month = dateTime.month,
                        dayOfMonth = dateTime.dayOfMonth,
                        hour = state.hour,
                        minute = state.minute,
                    )
                onConfirmButtonClick(selectedDateTime)
            },
            title =
                if (showingPicker.value) {
                    stringResource(id = R.string.select_time)
                } else {
                    stringResource(id = R.string.enter_time)
                },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    IconButton(onClick = { showingPicker.value = !showingPicker.value }) {
                        val icon =
                            if (showingPicker.value) {
                                Icons.Outlined.Keyboard
                            } else {
                                Icons.Outlined.Schedule
                            }
                        Icon(
                            imageVector = icon,
                            contentDescription =
                                if (showingPicker.value) {
                                    stringResource(id = R.string.switch_to_text_input)
                                } else {
                                    stringResource(id = R.string.switch_to_touch_input)
                                },
                        )
                    }
                }
            },
        ) {
            if (showingPicker.value && configuration.screenHeightDp > 400) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private object TodayAndOnwardsSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val utcTimeZone = TimeZone.UTC
        val yesterday = Clock.System.now().minus(1, DateTimeUnit.DAY, utcTimeZone)
        val yesterdayTimeMillis = yesterday.toEpochMilliseconds()

        // Check if provided date is from today onwards
        return utcTimeMillis >= yesterdayTimeMillis
    }
}
