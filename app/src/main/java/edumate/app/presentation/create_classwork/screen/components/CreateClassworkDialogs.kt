package edumate.app.presentation.create_classwork.screen.components

import android.annotation.SuppressLint
import android.webkit.URLUtil
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import java.util.Calendar
import java.util.Date

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddLinkDialog(
    openDialog: Boolean,
    onConfirm: (link: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (openDialog) {
        var link by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }
        val confirmEnabled = derivedStateOf { URLUtil.isValidUrl(link.text) }

        AlertDialog(onDismissRequest = onDismissRequest) {
            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(id = Strings.add_link),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = link,
                        onValueChange = {
                            link = it
                        },
                        modifier = Modifier.focusRequester(focusRequester),
                        label = {
                            Text(text = stringResource(id = Strings.url))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = onDismissRequest) {
                            Text(stringResource(id = Strings.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConfirm(link.text.trim())
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text(stringResource(id = Strings.add))
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDatePickerDialog(
    date: Date?,
    openDialog: Boolean,
    onConfirm: (date: Date) -> Unit,
    onDismissRequest: () -> Unit
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
                            onConfirm(cal.time)
                        }
                    },
                    enabled = confirmEnabled.value
                ) { Text(stringResource(id = Strings.ok)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text(stringResource(id = Strings.cancel)) }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = configuration.screenHeightDp > 400
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PointsDialog(
    openDialog: Boolean,
    currentPoint: String?,
    onConfirmClick: (points: String?) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (openDialog) {
        var point by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(currentPoint ?: "100"))
        }
        val tempPoint: MutableState<String?> = remember { mutableStateOf("") }

        point = TextFieldValue(currentPoint ?: "100")
        tempPoint.value = currentPoint

        val confirmEnabled = derivedStateOf { point.text.isNotBlank() }

        AlertDialog(onDismissRequest = onDismissRequest) {
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(id = Strings.change_point_value),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = tempPoint.value != null,
                                onClick = { tempPoint.value = point.text },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempPoint.value != null,
                            onClick = null
                        )
                        BasicTextField(
                            value = point,
                            onValueChange = {
                                if (it.text.length <= 3) {
                                    point = it
                                    tempPoint.value = it.text
                                }
                            },
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .widthIn(max = 500.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
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
                        Text(
                            text = stringResource(id = Strings._points, ""),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = tempPoint.value == null,
                                onClick = { tempPoint.value = null },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempPoint.value == null,
                            onClick = null
                        )
                        Text(
                            text = stringResource(id = Strings.unmarked),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = onDismissRequest) {
                            Text(stringResource(id = Strings.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConfirmClick(tempPoint.value?.trim())
                            },
                            enabled = if (tempPoint.value != null) confirmEnabled.value else true
                        ) {
                            Text(stringResource(id = Strings.save))
                        }
                    }
                }
            }
        }
    }
}