package app.edumate.presentation.stream.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.domain.model.member.Member
import app.edumate.domain.model.member.UserRole
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.stream.CommentsBottomSheetUiState
import app.edumate.presentation.stream.StreamUiEvent
import kotlin.collections.orEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    uiState: CommentsBottomSheetUiState,
    onEvent: (StreamUiEvent) -> Unit,
    replyAnnouncementId: String?,
    members: List<Member>,
    currentUserRole: CourseUserRole,
    currentUserId: String,
    onDismissRequest: () -> Unit,
) {
    if (replyAnnouncementId != null) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val snackbarHostState = remember { SnackbarHostState() }
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
                onEvent(StreamUiEvent.UserMessageShown)
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
                                    onEvent(StreamUiEvent.RetryComment(replyAnnouncementId))
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
                                        val commentUserRole =
                                            members
                                                .find {
                                                    it.userId == comment.creatorUserId
                                                }?.role ?: UserRole.STUDENT

                                        CommentListItem(
                                            comment = comment,
                                            itemUserRole = commentUserRole,
                                            currentUserRole = currentUserRole,
                                            currentUserId = currentUserId,
                                            onEditClick = { /*TODO*/ },
                                            onDeleteClick = { id ->
                                                onEvent(
                                                    StreamUiEvent.OnOpenDeleteCommentDialogChange(
                                                        id,
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
                                    onEvent(
                                        StreamUiEvent.AddComment(
                                            replyAnnouncementId,
                                            text,
                                        ),
                                    )
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
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
            onEvent(StreamUiEvent.OnOpenDeleteCommentDialogChange(null))
        },
        onConfirmButtonClick = {
            onEvent(
                StreamUiEvent.DeleteComment(
                    announcementId = replyAnnouncementId!!,
                    id = uiState.deleteCommentId!!,
                ),
            )
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
