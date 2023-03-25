package edumate.app.presentation.create_classwork

import android.net.Uri
import java.util.*

sealed class CreateClassworkUiEvent {
    data class OnDescriptionChange(val description: String) : CreateClassworkUiEvent()
    data class OnDueDateChange(val dueDate: Date?) : CreateClassworkUiEvent()
    data class OnGetContent(val uri: Uri) : CreateClassworkUiEvent()
    data class OnOpenAddLinkDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenAttachmentMenuChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenDatePickerDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenPointsDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenTimePickerDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnPointsChange(val points: String?) : CreateClassworkUiEvent()
    data class OnTitleChange(val title: String) : CreateClassworkUiEvent()
}