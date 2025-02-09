package app.edumate.presentation.viewStudentSubmission

sealed class ViewStudentSubmissionUiEvent {
    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : ViewStudentSubmissionUiEvent()

    data class OnOpenReturnDialogChange(
        val open: Boolean,
    ) : ViewStudentSubmissionUiEvent()

    data object Refresh : ViewStudentSubmissionUiEvent()

    data object Retry : ViewStudentSubmissionUiEvent()

    data object Return : ViewStudentSubmissionUiEvent()

    data object UserMessageShown : ViewStudentSubmissionUiEvent()
}
