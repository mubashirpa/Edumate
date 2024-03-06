package edumate.app.presentation.createClass

import androidx.compose.ui.text.input.TextFieldValue

sealed class CreateClassUiEvent {
    data class OnNameValueChange(val name: TextFieldValue) : CreateClassUiEvent()

    data class OnRoomValueChange(val room: String) : CreateClassUiEvent()

    data class OnSectionValueChange(val section: String) : CreateClassUiEvent()

    data class OnSubjectValueChange(val subject: String) : CreateClassUiEvent()

    data object CreateClass : CreateClassUiEvent()

    data object UserMessageShown : CreateClassUiEvent()
}
