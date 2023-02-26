package edumate.app.presentation.recover

import edumate.app.core.UiText

data class RecoverUiState(
    val openProgressDialog: Boolean = false,
    val isPasswordResetEmailSend: Boolean = false,
    val email: String = "",
    val emailError: UiText? = null,
    val userMessage: UiText? = null
)