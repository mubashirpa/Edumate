package edumate.app.presentation.teaching

sealed class TeachingUiEvent {
    data class OnDeleteCourse(val courseId: String) : TeachingUiEvent()
    data class OnOpenDeleteCourseDialogChange(val courseId: String?) : TeachingUiEvent()
    object OnRefresh : TeachingUiEvent()
    object UserMessageShown : TeachingUiEvent()
}