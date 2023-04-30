package edumate.app.presentation.enrolled

sealed class EnrolledUiEvent {
    data class Unenroll(val courseId: String) : EnrolledUiEvent()
    object OnRefresh : EnrolledUiEvent()
    object UserMessageShown : EnrolledUiEvent()
}