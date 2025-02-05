package app.edumate.presentation.viewCourseWork

import android.net.Uri
import app.edumate.domain.model.courseWork.CourseWorkType

sealed class ViewCourseWorkUiEvent {
    data class OnEditShortAnswerChange(
        val edit: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnFilePicked(
        val uri: Uri,
        val title: String,
    ) : ViewCourseWorkUiEvent()

    data class OnMultipleChoiceAnswerValueChange(
        val answer: String,
    ) : ViewCourseWorkUiEvent()

    data class OnOpenRemoveAttachmentDialogChange(
        val index: Int?,
    ) : ViewCourseWorkUiEvent()

    data class OnOpenTurnInDialogChange(
        val open: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnOpenUnSubmitDialogChange(
        val open: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnShowYourWorkBottomSheetChange(
        val show: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class RemoveAttachment(
        val position: Int,
    ) : ViewCourseWorkUiEvent()

    data class TurnIn(
        val workType: CourseWorkType?,
    ) : ViewCourseWorkUiEvent()

    data object Reclaim : ViewCourseWorkUiEvent()

    data object Refresh : ViewCourseWorkUiEvent()

    data object Retry : ViewCourseWorkUiEvent()

    data object UserMessageShown : ViewCourseWorkUiEvent()
}
