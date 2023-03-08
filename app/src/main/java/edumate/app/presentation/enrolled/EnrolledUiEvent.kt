package edumate.app.presentation.enrolled

sealed class EnrolledUiEvent {
    object FetchClasses : EnrolledUiEvent()
    data class Unenroll(val courseId: String) : EnrolledUiEvent()
    object UserMessageShown : EnrolledUiEvent()
}