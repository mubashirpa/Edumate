package edumate.app.presentation.createClass

sealed class CreateClassUiEvent {
    data class OnNameValueChange(val name: String) : CreateClassUiEvent()

    data class OnRoomValueChange(val room: String) : CreateClassUiEvent()

    data class OnSectionValueChange(val section: String) : CreateClassUiEvent()

    data class OnSubjectValueChange(val subject: String) : CreateClassUiEvent()

    data object OnCreateClick : CreateClassUiEvent()

    data object UserMessageShown : CreateClassUiEvent()
}
