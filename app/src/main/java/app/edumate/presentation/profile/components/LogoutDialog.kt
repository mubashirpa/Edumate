package app.edumate.presentation.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.edumate.R

@Composable
fun LogoutDialog(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    if (open) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Logout,
                    contentDescription = null,
                )
            },
            title = {
                Text(text = stringResource(R.string.logout))
            },
            text = {
                Text(text = stringResource(R.string.dialog_message_logout))
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirmation) {
                    Text(text = stringResource(R.string.logout))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }
}
