package app.edumate.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.User
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinCourseBottomSheet(
    show: Boolean,
    user: User?,
    onDismissRequest: () -> Unit,
    onJoinClass: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val textFieldState = rememberTextFieldState()

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            JoinCourseBottomSheetContent(
                user = user,
                textFieldState = textFieldState,
                onJoinClass = {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismissRequest()
                            onJoinClass()
                        }
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinCourseBottomSheetContent(
    user: User?,
    textFieldState: TextFieldState,
    onJoinClass: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    val maxWidthModifier = Modifier.fillMaxWidth()

    Column(modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(text = user?.name.orEmpty())
            },
            supportingContent = {
                Text(text = user?.email.orEmpty())
            },
            leadingContent = {
                UserAvatar(
                    id = user?.id.orEmpty(),
                    fullName = user?.name.orEmpty(),
                    photoUrl = user?.avatarUrl,
                )
            },
            colors = colors,
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(text = stringResource(R.string.join_class_description))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                state = textFieldState,
                modifier = maxWidthModifier,
                label = {
                    Text(text = stringResource(R.string.class_code))
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = { textFieldState.clearText() }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_text),
                            )
                        }
                    }
                },
                lineLimits = TextFieldLineLimits.SingleLine,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onJoinClass,
                modifier = maxWidthModifier,
            ) {
                Text(text = stringResource(R.string.join))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JoinCourseBottomSheetPreview() {
    EdumateTheme {
        JoinCourseBottomSheetContent(
            user =
                User(
                    email = "admin@edumate.app",
                    name = "Admin",
                ),
            textFieldState = rememberTextFieldState(initialText = "NzMzOTI4MzUzOdg1"),
            onJoinClass = {},
        )
    }
}
