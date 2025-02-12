package app.edumate.presentation.viewCourseWork

import app.edumate.domain.model.courseWork.CourseWorkType
import java.io.File

sealed class ViewCourseWorkUiEvent {
    data class AddLinkAttachment(
        val link: String,
    ) : ViewCourseWorkUiEvent()

    data class OnEditShortAnswerChange(
        val edit: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnFilePicked(
        val file: File,
        val title: String,
        val mimeType: String?,
        val size: Long?,
    ) : ViewCourseWorkUiEvent()

    data class OnMultipleChoiceAnswerValueChange(
        val answer: String,
    ) : ViewCourseWorkUiEvent()

    data class OnOpenAddLinkDialogChange(
        val open: Boolean,
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

    data class OnShowAddAttachmentBottomSheetChange(
        val show: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnShowCommentsBottomSheetChange(
        val show: Boolean,
    ) : ViewCourseWorkUiEvent()

    data class OnShowStudentSubmissionBottomSheetChange(
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

    data object RetryStudentSubmission : ViewCourseWorkUiEvent()

    data object UserMessageShown : ViewCourseWorkUiEvent()
}
