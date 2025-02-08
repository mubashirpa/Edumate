package app.edumate.presentation.studentWork

sealed class StudentWorkUiEvent {
    data object Refresh : StudentWorkUiEvent()

    data object Retry : StudentWorkUiEvent()

    data object UserMessageShown : StudentWorkUiEvent()
}
