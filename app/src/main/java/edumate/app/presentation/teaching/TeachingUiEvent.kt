package edumate.app.presentation.teaching

sealed class TeachingUiEvent {
    object OnRefresh : TeachingUiEvent()
    object UserMessageShown : TeachingUiEvent()
}