package app.edumate.presentation.courseDetails

sealed class CourseDetailsUiEvent {
    data object Retry : CourseDetailsUiEvent()

    data object ReviewDialogShown : CourseDetailsUiEvent()
}
