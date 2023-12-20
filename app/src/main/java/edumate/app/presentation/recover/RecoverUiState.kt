package edumate.app.presentation.recover

import edumate.app.core.UiText

data class RecoverUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isPasswordResetEmailSend: Boolean = false,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
