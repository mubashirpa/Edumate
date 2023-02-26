package edumate.app.presentation.recover

sealed class RecoverUiEvent {
    data class EmailChanged(val email: String) : RecoverUiEvent()
    object OnRecoverClick : RecoverUiEvent()
    object UserMessageShown : RecoverUiEvent()
}