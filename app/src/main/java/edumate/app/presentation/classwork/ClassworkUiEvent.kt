package edumate.app.presentation.classwork

sealed class ClassworkUiEvent {
    data class OnOpenFabMenuChange(val open: Boolean) : ClassworkUiEvent()
}