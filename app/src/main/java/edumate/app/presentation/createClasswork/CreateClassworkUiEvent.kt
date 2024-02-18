package edumate.app.presentation.createClasswork

import android.net.Uri
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import java.util.Calendar

sealed class CreateClassworkUiEvent {
    data class OnAddLinkAttachment(val link: String) : CreateClassworkUiEvent()

    data class OnDescriptionValueChange(val description: String) : CreateClassworkUiEvent()

    data class OnDueDateValueChange(val dueDate: Calendar?) : CreateClassworkUiEvent()

    data class OnFilePicked(val uri: Uri, val fileUtils: FileUtils) : CreateClassworkUiEvent()

    data class OnOpenAddLinkDialogChange(val openDialog: Boolean) : CreateClassworkUiEvent()

    data class OnOpenDatePickerDialogChange(val openDialog: Boolean) : CreateClassworkUiEvent()

    data class OnOpenPointsDialogChange(val openDialog: Boolean) : CreateClassworkUiEvent()

    data class OnOpenTimePickerDialogChange(val openDialog: Boolean) : CreateClassworkUiEvent()

    data class OnPointsValueChange(val points: String?) : CreateClassworkUiEvent()

    data class OnQuestionTypeDropdownExpandedChange(val expanded: Boolean) :
        CreateClassworkUiEvent()

    data class OnQuestionTypeSelectionOptionValueChange(val selectionOption: String) :
        CreateClassworkUiEvent()

    data class OnRemoveAttachment(val position: Int) : CreateClassworkUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(val showBottomSheet: Boolean) :
        CreateClassworkUiEvent()

    data class OnTitleValueChange(val title: String) : CreateClassworkUiEvent()

    data class OnWorkTypeValueChange(val workType: CourseWorkType) : CreateClassworkUiEvent()

    data object CreateCourseWork : CreateClassworkUiEvent()

    data object UserMessageShown : CreateClassworkUiEvent()
}
