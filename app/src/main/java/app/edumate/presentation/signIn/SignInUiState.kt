package app.edumate.presentation.signIn

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

data class SignInUiState(
    val email: TextFieldState = TextFieldState(),
    val emailError: UiText? = null,
    val isUserLoggedIn: Boolean = false,
    val openProgressDialog: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val passwordError: UiText? = null,
    val rememberPassword: Boolean = false,
    val userMessage: UiText? = null,
)
