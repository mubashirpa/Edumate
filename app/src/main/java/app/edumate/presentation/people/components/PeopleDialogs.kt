package app.edumate.presentation.people.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.user.User
import app.edumate.presentation.components.UserAvatar
import kotlinx.coroutines.launch

@Composable
fun DeletePersonDialog(
    user: User?,
    isTeacher: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: (userId: String) -> Unit,
) {
    if (user != null) {
        val userId = user.id
        val userName = user.name.orEmpty()
        val title =
            if (isTeacher) {
                stringResource(id = R.string.dialog_title_remove_teacher)
            } else {
                stringResource(id = R.string.dialog_title_remove_student)
            }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = title)
            },
            text = {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(
                        id = userId.orEmpty(),
                        fullName = userName,
                        photoUrl = user.photoUrl,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = userName)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let(onConfirmButtonClick)
                    },
                ) {
                    Text(stringResource(id = R.string.remove))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.share))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onShareClick()
                            }
                        }
                    },
                colors = colors,
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.copy_link))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onCopyClick()
                            }
                        }
                    },
                colors = colors,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
