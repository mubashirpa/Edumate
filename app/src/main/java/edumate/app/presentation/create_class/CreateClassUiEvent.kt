package edumate.app.presentation.create_class

sealed class CreateClassUiEvent {
    data class NameChanged(val name: String) : CreateClassUiEvent()

    data class RoomChanged(val room: String) : CreateClassUiEvent()

    data class SectionChanged(val section: String) : CreateClassUiEvent()

    data class SubjectChanged(val subject: String) : CreateClassUiEvent()

    data object OnCreateClick : CreateClassUiEvent()

    data object UserMessageShown : CreateClassUiEvent()
}
