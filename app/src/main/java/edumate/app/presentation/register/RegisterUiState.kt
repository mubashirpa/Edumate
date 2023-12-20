package edumate.app.presentation.register

import edumate.app.core.UiText

data class RegisterUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isUserLoggedIn: Boolean = false,
    val name: String = "",
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val password: String = "",
    val passwordError: UiText? = null,
    val userMessage: UiText? = null,
)
