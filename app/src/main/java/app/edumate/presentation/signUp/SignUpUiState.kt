package app.edumate.presentation.signUp

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

data class SignUpUiState(
    val email: TextFieldState = TextFieldState(),
    val emailError: UiText? = null,
    val isUserLoggedIn: Boolean = false,
    val name: TextFieldState = TextFieldState(),
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val passwordError: UiText? = null,
    val repeatedPassword: TextFieldState = TextFieldState(),
    val repeatedPasswordError: UiText? = null,
    val userMessage: UiText? = null,
)
