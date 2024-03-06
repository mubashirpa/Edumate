package edumate.app.presentation.recover

import androidx.compose.ui.text.input.TextFieldValue
import edumate.app.core.UiText

data class RecoverUiState(
    val email: TextFieldValue = TextFieldValue(text = ""),
    val emailError: UiText? = null,
    val isPasswordResetEmailSend: Boolean = false,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
