package edumate.app.presentation.enrolled.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings

@Composable
fun UnEnrolDialog(
    onDismissRequest: () -> Unit,
    openDialog: Boolean,
    onConfirmClick: () -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_unenrol_title))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_unenrol_text))
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(stringResource(id = Strings.unenrol))
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
