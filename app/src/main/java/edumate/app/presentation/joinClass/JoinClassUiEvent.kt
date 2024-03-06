package edumate.app.presentation.joinClass

sealed class JoinClassUiEvent {
    data class OnClassCodeValueChange(val classCode: String) : JoinClassUiEvent()

    data object JoinClass : JoinClassUiEvent()

    data object UserMessageShown : JoinClassUiEvent()
}
