package app.edumate.presentation.createCourseWork

import kotlinx.datetime.LocalDateTime
import java.io.File

sealed class CreateCourseWorkUiEvent {
    data class AddLinkAttachment(
        val link: String,
    ) : CreateCourseWorkUiEvent()

    data class OnDueTimeValueChange(
        val dateTime: LocalDateTime?,
    ) : CreateCourseWorkUiEvent()

    data class OnFilePicked(
        val file: File,
        val title: String,
    ) : CreateCourseWorkUiEvent()

    data class OnOpenAddLinkDialogChange(
        val open: Boolean,
    ) : CreateCourseWorkUiEvent()

    data class OnOpenDatePickerDialogChange(
        val open: Boolean,
    ) : CreateCourseWorkUiEvent()

    data class OnOpenPointsDialogChange(
        val open: Boolean,
    ) : CreateCourseWorkUiEvent()

    data class OnOpenTimePickerDialogChange(
        val open: Boolean,
    ) : CreateCourseWorkUiEvent()

    data class OnPointsValueChange(
        val points: String?,
    ) : CreateCourseWorkUiEvent()

    data class OnQuestionTypeDropdownExpandedChange(
        val expanded: Boolean,
    ) : CreateCourseWorkUiEvent()

    data class OnQuestionTypeValueChange(
        val selectionOptionIndex: Int,
    ) : CreateCourseWorkUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(
        val show: Boolean,
    ) : CreateCourseWorkUiEvent()

    data class RemoveAttachment(
        val position: Int,
    ) : CreateCourseWorkUiEvent()

    data object CreateCourseWork : CreateCourseWorkUiEvent()

    data object UserMessageShown : CreateCourseWorkUiEvent()
}
