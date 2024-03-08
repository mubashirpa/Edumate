package edumate.app.presentation.classDetails

sealed class ClassDetailsUiEvent {
    data object Retry : ClassDetailsUiEvent()
}
