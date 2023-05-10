package edumate.app.presentation.enrolled.screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings

@Composable
fun UnEnrolDialog(
    onDismissRequest: () -> Unit,
    courseId: String?,
    onConfirmClick: (courseId: String) -> Unit
) {
    if (courseId != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_unenrol_title))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_unenrol_text))
            },
            confirmButton = {
                TextButton(onClick = { onConfirmClick(courseId) }) {
                    Text(stringResource(id = Strings.unenrol))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}