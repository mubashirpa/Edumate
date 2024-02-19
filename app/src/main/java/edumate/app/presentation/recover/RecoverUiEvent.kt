package edumate.app.presentation.recover

sealed class RecoverUiEvent {
    data class OnEmailValueChange(val email: String) : RecoverUiEvent()

    data object Recover : RecoverUiEvent()

    data object UserMessageShown : RecoverUiEvent()
}
