package edumate.app.presentation.recover

import androidx.compose.ui.text.input.TextFieldValue

sealed class RecoverUiEvent {
    data class OnEmailValueChange(val email: TextFieldValue) : RecoverUiEvent()

    data object Recover : RecoverUiEvent()

    data object UserMessageShown : RecoverUiEvent()
}
