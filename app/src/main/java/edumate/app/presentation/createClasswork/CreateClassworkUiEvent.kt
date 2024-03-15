package edumate.app.presentation.createClasswork

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.datetime.LocalDateTime

sealed class CreateClassworkUiEvent {
    data class OnAddLinkAttachment(val link: String) : CreateClassworkUiEvent()

    data class OnDescriptionValueChange(val description: String) : CreateClassworkUiEvent()

    data class OnDueDateTimeValueChange(val dateTime: LocalDateTime?) : CreateClassworkUiEvent()

    data class OnFilePicked(val uri: Uri, val title: String) : CreateClassworkUiEvent()

    data class OnOpenAddLinkDialogChange(val open: Boolean) : CreateClassworkUiEvent()

    data class OnOpenDatePickerDialogChange(val open: Boolean) : CreateClassworkUiEvent()

    data class OnOpenPointsDialogChange(val open: Boolean) : CreateClassworkUiEvent()

    data class OnOpenTimePickerDialogChange(val open: Boolean) : CreateClassworkUiEvent()

    data class OnPointsValueChange(val points: String?) : CreateClassworkUiEvent()

    data class OnQuestionTypeDropdownExpandedChange(val expanded: Boolean) :
        CreateClassworkUiEvent()

    data class OnQuestionTypeValueChange(val selectionOptionIndex: Int) :
        CreateClassworkUiEvent()

    data class OnRemoveAttachment(val position: Int) : CreateClassworkUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(val show: Boolean) : CreateClassworkUiEvent()

    data class OnTitleValueChange(val title: TextFieldValue) : CreateClassworkUiEvent()

    data object CreateCourseWork : CreateClassworkUiEvent()

    data object UserMessageShown : CreateClassworkUiEvent()
}
