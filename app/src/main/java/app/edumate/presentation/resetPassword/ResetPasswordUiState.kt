package app.edumate.presentation.resetPassword

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

data class ResetPasswordUiState(
    val email: TextFieldState = TextFieldState(),
    val emailError: UiText? = null,
    val isResetPassword: Boolean = false,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
