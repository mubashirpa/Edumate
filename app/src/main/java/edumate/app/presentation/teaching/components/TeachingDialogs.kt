package edumate.app.presentation.teaching.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings

@Composable
fun DeleteCourseDialog(
    onDismissRequest: () -> Unit,
    open: Boolean,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_title_delete_course))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_message_delete_course))
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(stringResource(id = Strings.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            },
        )
    }
}

@Composable
fun LeaveCourseDialog(
    onDismissRequest: () -> Unit,
    open: Boolean,
    name: String,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_title_leave_course, name))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_message_leave_course))
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(stringResource(id = Strings.leave_class))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            },
        )
    }
}
