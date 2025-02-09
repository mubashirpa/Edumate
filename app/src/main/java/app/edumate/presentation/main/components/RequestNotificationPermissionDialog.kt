package app.edumate.presentation.main.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.edumate.R

@Composable
fun RequestNotificationPermissionDialog(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    },
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(R.string.dismiss))
                }
            },
            title = {
                Text(text = stringResource(R.string.dialog_title_request_notification_permission))
            },
            text = {
                Text(text = stringResource(R.string.dialog_message_request_notification_permission))
            },
        )
    }
}
