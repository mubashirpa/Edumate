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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.UiText
import app.edumate.domain.model.User
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.home.JoinCourseBottomSheetUiState
import app.edumate.presentation.theme.EdumateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinCourseBottomSheet(
    uiState: JoinCourseBottomSheetUiState,
    user: User?,
    onDismissRequest: () -> Unit,
    onJoinCourse: (courseId: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            JoinCourseBottomSheetContent(
                courseId = uiState.courseId,
                user = user,
                courseIdError = uiState.courseIdError,
                onJoinCourse = { courseId ->
                    onJoinCourse(courseId)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinCourseBottomSheetContent(
    courseId: TextFieldState,
    user: User?,
    courseIdError: UiText?,
    onJoinCourse: (courseId: String) -> Unit,
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
                state = courseId,
                modifier = maxWidthModifier,
                label = {
                    Text(text = stringResource(R.string.class_code))
                },
                trailingIcon = {
                    if (courseId.text.isNotEmpty()) {
                        IconButton(onClick = { courseId.clearText() }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_text),
                            )
                        }
                    }
                },
                supportingText =
                    courseIdError?.let {
                        { Text(text = it.asString(), modifier = Modifier.clearAndSetSemantics {}) }
                    },
                isError = courseIdError != null,
                lineLimits = TextFieldLineLimits.SingleLine,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onJoinCourse(courseId.text.toString())
                },
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
            courseId = rememberTextFieldState(initialText = "NzMzOTI4MzUzOdg1"),
            user =
                User(
                    email = "admin@edumate.app",
                    name = "Admin",
                ),
            courseIdError = null,
            onJoinCourse = {},
        )
    }
}
