package edumate.app.presentation.enrolled

sealed class EnrolledUiEvent {
    data class OnOpenUnEnrolDialogChange(val courseId: String?) : EnrolledUiEvent()

    data class OnUnEnroll(val courseId: String) : EnrolledUiEvent()

    data object OnRefresh : EnrolledUiEvent()

    data object UserMessageShown : EnrolledUiEvent()
}
