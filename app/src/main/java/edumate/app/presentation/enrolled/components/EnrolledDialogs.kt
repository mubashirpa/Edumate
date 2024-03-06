package edumate.app.presentation.enrolled.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings

@Composable
fun UnEnrollDialog(
    onDismissRequest: () -> Unit,
    open: Boolean,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_title_unenroll))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_message_unenroll))
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(stringResource(id = Strings.unenroll))
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
