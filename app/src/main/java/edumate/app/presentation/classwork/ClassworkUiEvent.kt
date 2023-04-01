package edumate.app.presentation.classwork

sealed class ClassworkUiEvent {
    data class OnDelete(val courseWorkId: String, val courseId: String) : ClassworkUiEvent()
    data class OnOpenFabMenuChange(val open: Boolean) : ClassworkUiEvent()
    object OnRefresh : ClassworkUiEvent()
    object OnRetry : ClassworkUiEvent()
    object UserMessageShown : ClassworkUiEvent()
}