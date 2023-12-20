package edumate.app.presentation.view_student_work

import edumate.app.domain.model.course_work.CourseWork

sealed class ViewStudentWorkUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : ViewStudentWorkUiEvent()

    data class OnCreate(val courseWork: CourseWork) : ViewStudentWorkUiEvent()

    data class OnGradeChange(val grade: String) : ViewStudentWorkUiEvent()

    data class OnOpenReturnDialog(val open: Boolean) : ViewStudentWorkUiEvent()

    data object OnRefresh : ViewStudentWorkUiEvent()

    data object PatchStudentWork : ViewStudentWorkUiEvent()

    data object ReturnStudentWork : ViewStudentWorkUiEvent()

    data object UserMessageShown : ViewStudentWorkUiEvent()
}
