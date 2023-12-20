package edumate.app.presentation.student_work

sealed class StudentWorkUiEvent {
    data class OnInit(val courseId: String, val courseWorkId: String) : StudentWorkUiEvent()
    data object OnRefresh : StudentWorkUiEvent()
    data object UserMessageShown : StudentWorkUiEvent()
}