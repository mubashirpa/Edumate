package edumate.app.presentation.recover

sealed class RecoverUiEvent {
    data class EmailChanged(val email: String) : RecoverUiEvent()
    data object OnRecoverClick : RecoverUiEvent()
    data object UserMessageShown : RecoverUiEvent()
}