package edumate.app.presentation.create_classwork.screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.util.Calendar
import java.util.Date
import edumate.app.R.string as Strings

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDatePickerDialog(
    onDismissRequest: () -> Unit,
    date: Date?,
    openDialog: Boolean,
    onConfirmClick: (date: Date) -> Unit,
) {
    // TODO("Block previous days from being selected")
    if (openDialog) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = derivedStateOf { datePickerState.selectedDateMillis != null }
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
                            val cal = Calendar.getInstance()
                            var hour = 0
                            var minute = 0
                            if (date != null) {
                                cal.time = date
                                hour = cal.get(Calendar.HOUR_OF_DAY)
                                minute = cal.get(Calendar.MINUTE)
                            }
                            cal.time = Date(selectedDateMillis)
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            cal.isLenient = false
                            onConfirmClick(cal.time)
                        }
                    },
                    enabled = confirmEnabled.value,
                ) { Text(stringResource(id = Strings.ok)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text(stringResource(id = Strings.cancel)) }
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
    onDismissRequest: () -> Unit,
    openDialog: Boolean,
    currentPoint: String?,
    onConfirmClick: (points: String?) -> Unit,
) {
    if (openDialog) {
        var point by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(currentPoint ?: "100"))
        }
        val tempPoint: MutableState<String?> = remember { mutableStateOf("") }

        point = TextFieldValue(currentPoint ?: "100")
        tempPoint.value = currentPoint

        val confirmEnabled = derivedStateOf { point.text.isNotBlank() }

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
                        text = stringResource(id = Strings.change_point_value),
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
                                )
                                .padding(horizontal = 16.dp),
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
                                text = stringResource(id = Strings._points, ""),
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
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = tempPoint.value == null,
                                onClick = null,
                            )
                            Text(
                                text = stringResource(id = Strings.unmarked),
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
                            Text(stringResource(id = Strings.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConfirmClick(tempPoint.value?.trim())
                            },
                            enabled = if (tempPoint.value != null) confirmEnabled.value else true,
                        ) {
                            Text(stringResource(id = Strings.save))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentTimePickerDialog(
    onDismissRequest: () -> Unit,
    date: Date?,
    openDialog: Boolean,
    onConfirmClick: (date: Date) -> Unit,
) {
    val state = rememberTimePickerState(initialHour = 0, initialMinute = 0)
    val showingPicker = remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current

    if (openDialog) {
        TimePickerDialog(
            title =
                if (showingPicker.value) {
                    stringResource(id = Strings.select_time)
                } else {
                    stringResource(id = Strings.enter_time)
                },
            onCancel = { onDismissRequest() },
            onConfirm = {
                onDismissRequest()
                date?.let {
                    val cal = Calendar.getInstance()
                    cal.time = it
                    cal.set(Calendar.HOUR_OF_DAY, state.hour)
                    cal.set(Calendar.MINUTE, state.minute)
                    cal.isLenient = false
                    onConfirmClick(cal.time)
                }
            },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    // Make this take the entire viewport. This will guarantee that Screen readers
                    // focus the toggle first.
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .semantics {
                                    isTraversalGroup = true
                                },
                    ) {
                        IconButton(
                            modifier =
                                Modifier
                                    .padding(24.dp)
                                    // This is a workaround so that the Icon comes up first
                                    // in the talkback traversal order. So that users of a11y
                                    // services can use the text input. When talkback traversal
                                    // order is customizable we can remove this.
                                    .align(Alignment.BottomStart)
                                    .zIndex(5f),
                            onClick = { showingPicker.value = !showingPicker.value },
                        ) {
                            val icon =
                                if (showingPicker.value) {
                                    Icons.Outlined.Keyboard
                                } else {
                                    Icons.Outlined.Schedule
                                }
                            Icon(
                                icon,
                                contentDescription =
                                    if (showingPicker.value) {
                                        stringResource(id = Strings.switch_to_text_input)
                                    } else {
                                        stringResource(id = Strings.switch_to_touch_input)
                                    },
                            )
                        }
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
