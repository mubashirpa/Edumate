package app.edumate.presentation.stream

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.comment.Comment

data class CommentsBottomSheetUiState(
    val comment: TextFieldState = TextFieldState(),
    val commentsResult: Result<List<Comment>> = Result.Empty(),
    val deleteCommentId: String? = null,
    val editCommentId: String? = null,
    val isLoading: Boolean = false,
    val userMessage: UiText? = null,
)
