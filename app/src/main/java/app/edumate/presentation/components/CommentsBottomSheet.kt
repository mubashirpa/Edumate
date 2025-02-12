package app.edumate.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.comment.Comment
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.stream.components.DeleteCommentDialog
import kotlin.collections.orEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    uiState: CommentsBottomSheetUiState,
    onEvent: (CommentsBottomSheetUiEvent) -> Unit,
    show: Boolean,
    currentUserRole: CourseUserRole,
    currentUserId: String,
    onDismissRequest: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val snackbarHostState = remember { SnackbarHostState() }
        val focusRequester = remember { FocusRequester() }
        val context = LocalContext.current
        val isFullscreen = bottomSheetState.targetValue == SheetValue.Expanded
        val cornerSize by animateDpAsState(
            targetValue = if (isFullscreen) 0.dp else 28.dp,
            label = stringResource(id = R.string.label_animate_bottom_sheet_corner_size),
        )
        val paddingValues = WindowInsets.systemBars.asPaddingValues()

        uiState.userMessage?.let { userMessage ->
            LaunchedEffect(userMessage) {
                snackbarHostState.showSnackbar(userMessage.asString(context))
                // Once the message is displayed and dismissed, notify the ViewModel.
                onEvent(CommentsBottomSheetUiEvent.UserMessageShown)
            }
        }

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize),
            dragHandle = {
                val topPadding by animateDpAsState(
                    targetValue = if (isFullscreen) paddingValues.calculateTopPadding() else 0.dp,
                    label = stringResource(id = R.string.label_animate_bottom_sheet_top_padding),
                )
                BottomSheetDefaults.DragHandle(modifier = Modifier.padding(top = topPadding))
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    when (val commentsResult = uiState.commentsResult) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            ErrorScreen(
                                onRetryClick = {
                                    onEvent(CommentsBottomSheetUiEvent.Retry)
                                },
                                modifier = Modifier.fillMaxSize(),
                                errorMessage = commentsResult.message!!.asString(),
                            )
                        }

                        is Result.Loading -> {
                            LoadingScreen()
                        }

                        is Result.Success -> {
                            val comments = commentsResult.data.orEmpty()

                            LazyColumn(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(bottom = 12.dp),
                                content = {
                                    items(
                                        items = comments,
                                        key = { it.id!! },
                                    ) { comment ->
                                        CommentListItem(
                                            comment = comment,
                                            currentUserRole = currentUserRole,
                                            currentUserId = currentUserId,
                                            selected = comment.id == uiState.editCommentId,
                                            onEditClick = { id ->
                                                onEvent(
                                                    CommentsBottomSheetUiEvent.OnEditComment(
                                                        id,
                                                        comment.text.orEmpty(),
                                                    ),
                                                )
                                                focusRequester.requestFocus()
                                            },
                                            onDeleteClick = { id ->
                                                onEvent(
                                                    CommentsBottomSheetUiEvent.OnOpenDeleteCommentDialogChange(
                                                        id,
                                                    ),
                                                )
                                            },
                                            onClearSelection = {
                                                onEvent(
                                                    CommentsBottomSheetUiEvent.OnEditComment(
                                                        null,
                                                        "",
                                                    ),
                                                )
                                            },
                                        )
                                    }
                                },
                            )
                            HorizontalDivider(thickness = Dp.Hairline)
                            if (uiState.isLoading) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                            CommentTextField(
                                state = uiState.comment,
                                enabled = !uiState.isLoading,
                                onSendClick = { text ->
                                    onEvent(CommentsBottomSheetUiEvent.AddComment(text.trim()))
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .focusRequester(focusRequester),
                            )
                        }
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp),
                )
            }
        }
    }

    DeleteCommentDialog(
        open = uiState.deleteCommentId != null,
        onDismissRequest = {
            onEvent(CommentsBottomSheetUiEvent.OnOpenDeleteCommentDialogChange(null))
        },
        onConfirmButtonClick = {
            uiState.deleteCommentId?.let { commentId ->
                onEvent(CommentsBottomSheetUiEvent.DeleteComment(commentId))
            }
        },
    )
}

@Composable
private fun CommentTextField(
    state: TextFieldState,
    enabled: Boolean,
    onSendClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        placeholder = {
            Text(text = stringResource(R.string.add_a_comment))
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onSendClick(state.text.toString())
                },
                enabled = enabled,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                )
            }
        },
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = true,
                imeAction = ImeAction.Send,
            ),
        onKeyboardAction = {
            onSendClick(state.text.toString())
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = CircleShape,
    )
}

data class CommentsBottomSheetUiState(
    val comment: TextFieldState = TextFieldState(),
    val commentsResult: Result<List<Comment>> = Result.Empty(),
    val deleteCommentId: String? = null,
    val editCommentId: String? = null,
    val isLoading: Boolean = false,
    val userMessage: UiText? = null,
)

sealed class CommentsBottomSheetUiEvent {
    data class AddComment(
        val text: String,
    ) : CommentsBottomSheetUiEvent()

    data class DeleteComment(
        val commentId: String,
    ) : CommentsBottomSheetUiEvent()

    data class OnEditComment(
        val commentId: String?,
        val text: String,
    ) : CommentsBottomSheetUiEvent()

    data class OnOpenDeleteCommentDialogChange(
        val commentId: String?,
    ) : CommentsBottomSheetUiEvent()

    data object Retry : CommentsBottomSheetUiEvent()

    data object UserMessageShown : CommentsBottomSheetUiEvent()
}
