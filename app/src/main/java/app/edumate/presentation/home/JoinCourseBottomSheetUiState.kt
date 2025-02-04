package app.edumate.presentation.home

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

data class JoinCourseBottomSheetUiState(
    val courseId: TextFieldState = TextFieldState(),
    val courseIdError: UiText? = null,
    val error: UiText? = null,
)
