package edumate.app.presentation.teaching

sealed class TeachingUiEvent {
    data class DeleteCourse(val courseId: String) : TeachingUiEvent()

    data class OnOpenDeleteCourseDialogChange(val courseId: String?) : TeachingUiEvent()

    data object Refresh : TeachingUiEvent()

    data object UserMessageShown : TeachingUiEvent()
}
