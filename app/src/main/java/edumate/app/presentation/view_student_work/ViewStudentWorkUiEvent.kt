package edumate.app.presentation.view_student_work

import edumate.app.domain.model.course_work.CourseWork

sealed class ViewStudentWorkUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : ViewStudentWorkUiEvent()
    data class OnCreate(val courseWork: CourseWork) : ViewStudentWorkUiEvent()
    data class OnGradeChange(val grade: String) : ViewStudentWorkUiEvent()
    data class OnOpenReturnDialog(val open: Boolean) : ViewStudentWorkUiEvent()
    object OnRefresh : ViewStudentWorkUiEvent()
    object PatchStudentWork : ViewStudentWorkUiEvent()
    object ReturnStudentWork : ViewStudentWorkUiEvent()
    object UserMessageShown : ViewStudentWorkUiEvent()
}