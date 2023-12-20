package edumate.app.presentation.teaching

sealed class TeachingUiEvent {
    data class OnDeleteCourse(val courseId: String) : TeachingUiEvent()
    data class OnOpenDeleteCourseDialogChange(val courseId: String?) : TeachingUiEvent()
    data object OnRefresh : TeachingUiEvent()
    data object UserMessageShown : TeachingUiEvent()
}