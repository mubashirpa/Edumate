package edumate.app.presentation.create_classwork.screen.components

import android.annotation.SuppressLint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import java.util.*

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