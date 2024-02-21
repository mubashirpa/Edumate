package edumate.app.presentation.enrolled

sealed class EnrolledUiEvent {
    data class OnOpenUnEnrolDialogChange(val courseId: String?) : EnrolledUiEvent()

    data class UnEnroll(val courseId: String) : EnrolledUiEvent()

    data object Refresh : EnrolledUiEvent()

    data object UserMessageShown : EnrolledUiEvent()
}
