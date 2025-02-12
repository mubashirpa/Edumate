package app.edumate.presentation.createCourse

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

data class CreateCourseUiState(
    val isLoading: Boolean = false,
    val name: TextFieldState = TextFieldState(),
    val nameError: UiText? = null,
    val newCourseId: String? = null,
    val openProgressDialog: Boolean = false,
    val room: TextFieldState = TextFieldState(),
    val section: TextFieldState = TextFieldState(),
    val subject: TextFieldState = TextFieldState(),
    val userMessage: UiText? = null,
)
