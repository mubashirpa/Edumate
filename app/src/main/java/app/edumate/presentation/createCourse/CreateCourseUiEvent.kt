package app.edumate.presentation.createCourse

sealed class CreateCourseUiEvent {
    data object CreateCourse : CreateCourseUiEvent()

    data object UserMessageShown : CreateCourseUiEvent()
}
