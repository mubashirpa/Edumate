package edumate.app.presentation.create_classwork.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import edumate.app.R.string as Strings
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentTimePickerDialog(
    date: Date?,
    openDialog: Boolean,
    onConfirm: (date: Date) -> Unit,
    onDismissRequest: () -> Unit
) {
    val state = rememberTimePickerState(initialHour = 0, initialMinute = 0)
    val showingPicker = remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current

    if (openDialog) {
        TimePickerDialog(
            title = if (showingPicker.value) {
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
                    onConfirm(cal.time)
                }
            },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    Box(
                        Modifier.semantics { isContainer = true }
                    ) {
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .zIndex(5f),
                            onClick = { showingPicker.value = !showingPicker.value }
                        ) {
                            val icon = if (showingPicker.value) {
                                Icons.Outlined.Keyboard
                            } else {
                                Icons.Outlined.Schedule
                            }
                            Icon(
                                icon,
                                contentDescription = if (showingPicker.value) {
                                    stringResource(id = Strings.switch_to_text_input)
                                } else {
                                    stringResource(id = Strings.switch_to_touch_input)
                                }
                            )
                        }
                    }
                }
            }
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
@Composable
private fun TimePickerDialog(
    title: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    AlertDialog(onDismissRequest = onCancel) {
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
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                content()
                Row(modifier = Modifier.fillMaxWidth()) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onCancel) {
                        Text(stringResource(id = Strings.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) {
                        Text(stringResource(id = Strings.ok))
                    }
                }
            }
        }
    }
}