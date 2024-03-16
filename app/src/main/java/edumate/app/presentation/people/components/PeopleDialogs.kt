package edumate.app.presentation.people.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import edumate.app.domain.model.userProfiles.UserProfile
import edumate.app.presentation.components.UserAvatar
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@Composable
fun DeleteUserDialog(
    onDismissRequest: () -> Unit,
    open: Boolean,
    userProfile: UserProfile?,
    isTeacher: Boolean,
    onConfirmButtonClick: (userId: String, isTeacher: Boolean) -> Unit,
) {
    if (open) {
        val userId = userProfile?.id
        val userName = userProfile?.name?.fullName.orEmpty()
        val title =
            if (isTeacher) {
                stringResource(id = Strings.dialog_title_remove_teacher)
            } else {
                stringResource(id = Strings.dialog_title_remove_student)
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
                        photoUrl = userProfile?.photoUrl,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = userName)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let {
                            onConfirmButtonClick(it, isTeacher)
                        }
                    },
                ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteBottomSheet(
    onDismissRequest: () -> Unit,
    show: Boolean,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = Strings.share))
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
                    Text(text = stringResource(id = Strings.copy_link))
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
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }
}
