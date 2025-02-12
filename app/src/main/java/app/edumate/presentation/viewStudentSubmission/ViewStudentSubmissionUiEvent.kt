package app.edumate.presentation.viewStudentSubmission

sealed class ViewStudentSubmissionUiEvent {
    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : ViewStudentSubmissionUiEvent()

    data class OnOpenReturnDialogChange(
        val open: Boolean,
    ) : ViewStudentSubmissionUiEvent()

    data class OnShowCommentsBottomSheetChange(
        val show: Boolean,
    ) : ViewStudentSubmissionUiEvent()

    data class Return(
        val id: String,
        val grade: Int? = null,
    ) : ViewStudentSubmissionUiEvent()

    data object Refresh : ViewStudentSubmissionUiEvent()

    data object Retry : ViewStudentSubmissionUiEvent()

    data object UserMessageShown : ViewStudentSubmissionUiEvent()
}
