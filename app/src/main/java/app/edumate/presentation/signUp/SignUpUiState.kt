package app.edumate.presentation.signUp

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.UiText

private typealias Success = Boolean
private typealias Verified = Boolean

data class SignUpUiState(
    val email: TextFieldState = TextFieldState(),
    val emailError: UiText? = null,
    val userLoginStatus: Pair<Success, Verified> = Pair(false, false),
    val name: TextFieldState = TextFieldState(),
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val passwordError: UiText? = null,
    val repeatedPassword: TextFieldState = TextFieldState(),
    val repeatedPasswordError: UiText? = null,
    val userMessage: UiText? = null,
)
