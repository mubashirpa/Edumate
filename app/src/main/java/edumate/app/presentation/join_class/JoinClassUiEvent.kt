package edumate.app.presentation.join_class

sealed class JoinClassUiEvent {
    data class ClassCodeChanged(val classCode: String) : JoinClassUiEvent()
    object OnJoinClick : JoinClassUiEvent()
    object UserMessageShown : JoinClassUiEvent()
}