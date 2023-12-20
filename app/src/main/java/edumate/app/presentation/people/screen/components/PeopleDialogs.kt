package edumate.app.presentation.people.screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.UserAvatar
import edumate.app.R.string as Strings

@Composable
fun LeaveClassDialog(
    onDismissRequest: () -> Unit,
    openDialog: Boolean,
    onConfirmClick: () -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.leave_class))
            },
            text = {
                Text(text = stringResource(id = Strings.leave_class_warning_message))
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
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

@Composable
fun RemoveUserDialog(
    onDismissRequest: () -> Unit,
    userProfile: UserProfile?,
    userType: UserType,
    onConfirmClick: (userType: UserType, uid: String) -> Unit,
) {
    if (userProfile != null) {
        val userId = userProfile.id
        val title =
            if (userType == UserType.TEACHER) {
                stringResource(id = Strings.remove_teacher)
            } else {
                stringResource(id = Strings.remove_student)
            }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(
                        id = userId,
                        fullName = userProfile.displayName ?: userProfile.emailAddress.orEmpty(),
                        photoUrl = userProfile.photoUrl,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = userProfile.displayName.orEmpty())
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirmClick(userType, userId) }) {
                    Text(stringResource(id = Strings.remove))
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
