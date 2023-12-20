package edumate.app.presentation.stream.screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings

@Composable
fun DeleteAnnouncementDialog(
    onDismissRequest: () -> Unit,
    announcementId: String?,
    onConfirmClick: (id: String) -> Unit,
) {
    if (announcementId != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_delete_announcement_title))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_delete_announcement_text))
            },
            confirmButton = {
                TextButton(onClick = { onConfirmClick(announcementId) }) {
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
