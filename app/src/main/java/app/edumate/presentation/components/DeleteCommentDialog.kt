package app.edumate.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.edumate.R

@Composable
fun DeleteCommentDialog(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.dialog_title_delete_comment))
            },
            text = {
                Text(text = stringResource(id = R.string.dialog_message_delete_comment))
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
        )
    }
}
