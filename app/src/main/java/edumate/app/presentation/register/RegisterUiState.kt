package edumate.app.presentation.register

import edumate.app.core.UiText

data class RegisterUiState(
    val openProgressDialog: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val userMessage: UiText? = null
)