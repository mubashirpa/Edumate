package edumate.app.presentation.create_room

sealed class CreateRoomUiEvent {
    data class NameChanged(val name: String) : CreateRoomUiEvent()
    data class SectionChanged(val section: String) : CreateRoomUiEvent()
    data class SubjectChanged(val subject: String) : CreateRoomUiEvent()
    object OnCreateClick : CreateRoomUiEvent()
}