package edumate.app.presentation.create_classwork

import android.net.Uri
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import java.util.*

sealed class CreateClassworkUiEvent {
    data class OnAddLinkAttachment(val link: String) : CreateClassworkUiEvent()
    data class OnDescriptionChange(val description: String) : CreateClassworkUiEvent()
    data class OnDueDateChange(val dueDate: Date?) : CreateClassworkUiEvent()
    data class OnFilePicked(val uri: Uri, val fileUtils: FileUtils) : CreateClassworkUiEvent()
    data class OnOpenAddLinkDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenAttachmentMenuChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenDatePickerDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenPointsDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnOpenTimePickerDialogChange(val open: Boolean) : CreateClassworkUiEvent()
    data class OnPointsChange(val points: String?) : CreateClassworkUiEvent()
    data class OnTitleChange(val title: String) : CreateClassworkUiEvent()
    data class OnRemoveAttachment(val index: Int) : CreateClassworkUiEvent()
    data class OnWorkTypeChange(val workType: CourseWorkType) : CreateClassworkUiEvent()
    object CreateClasswork : CreateClassworkUiEvent()
    object UserMessageShown : CreateClassworkUiEvent()
}