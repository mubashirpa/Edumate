package edumate.app.presentation.view_student_work

sealed class ViewStudentWorkUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : ViewStudentWorkUiEvent()
    data class OnGradeChange(val grade: String) : ViewStudentWorkUiEvent()
    data class OnOpenReturnDialog(val open: Boolean) : ViewStudentWorkUiEvent()
    object OnRefresh : ViewStudentWorkUiEvent()
    object PatchStudentWork : ViewStudentWorkUiEvent()
    object ReturnStudentWork : ViewStudentWorkUiEvent()
    object UserMessageShown : ViewStudentWorkUiEvent()
}