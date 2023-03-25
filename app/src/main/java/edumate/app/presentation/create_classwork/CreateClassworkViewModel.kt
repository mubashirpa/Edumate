package edumate.app.presentation.create_classwork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateClassworkViewModel : ViewModel() {

    var uiState by mutableStateOf(CreateClassworkUiState())
        private set

    fun onEvent(event: CreateClassworkUiEvent) {
        when (event) {
            is CreateClassworkUiEvent.OnDescriptionChange -> {
                uiState = uiState.copy(description = event.description)
            }
            is CreateClassworkUiEvent.OnDueDateChange -> {
                uiState = uiState.copy(dueDate = event.dueDate)
            }
            is CreateClassworkUiEvent.OnGetContent -> {
                val attachments: MutableList<String> = uiState.attachments.toMutableList()
                attachments.add(event.uri.toString())
                uiState = uiState.copy(attachments = attachments)
            }
            is CreateClassworkUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }
            is CreateClassworkUiEvent.OnOpenAttachmentMenuChange -> {
                uiState = uiState.copy(openAttachmentMenu = event.open)
            }
            is CreateClassworkUiEvent.OnOpenDatePickerDialogChange -> {
                uiState = uiState.copy(openDatePickerDialog = event.open)
            }
            is CreateClassworkUiEvent.OnOpenPointsDialogChange -> {
                uiState = uiState.copy(openPointsDialog = event.open)
            }
            is CreateClassworkUiEvent.OnOpenTimePickerDialogChange -> {
                uiState = uiState.copy(openTimePickerDialog = event.open)
            }
            is CreateClassworkUiEvent.OnPointsChange -> {
                uiState = uiState.copy(points = event.points)
            }
            is CreateClassworkUiEvent.OnTitleChange -> {
                uiState = uiState.copy(title = event.title)
            }
        }
    }
}