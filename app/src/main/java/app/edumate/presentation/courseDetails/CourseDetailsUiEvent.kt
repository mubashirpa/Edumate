package app.edumate.presentation.courseDetails

sealed class CourseDetailsUiEvent {
    data object OnRetry : CourseDetailsUiEvent()
}
