package edumate.app.presentation.login

import edumate.app.core.UiText

data class LoginUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isUserLoggedIn: Boolean = false,
    val openProgressDialog: Boolean = false,
    val password: String = "",
    val passwordError: UiText? = null,
    val userMessage: UiText? = null,
)
