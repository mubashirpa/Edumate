package edumate.app.presentation.login

import edumate.app.core.UiText

data class LoginUiState(
    val openProgressDialog: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val email: String = "",
    val password: String = "",
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val userMessage: UiText? = null
)