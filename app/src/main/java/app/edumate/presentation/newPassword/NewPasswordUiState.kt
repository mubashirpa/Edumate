package app.edumate.presentation.newPassword

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

data class NewPasswordUiState(
    val isUpdatePassword: Boolean = false,
    val openProgressDialog: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val passwordError: UiText? = null,
    val repeatedPassword: TextFieldState = TextFieldState(),
    val repeatedPasswordError: UiText? = null,
    val userMessage: UiText? = null,
)
