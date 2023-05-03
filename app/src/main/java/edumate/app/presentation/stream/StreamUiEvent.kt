package edumate.app.presentation.stream

sealed class StreamUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : StreamUiEvent()
    object OnRefresh : StreamUiEvent()
    object UserMessageShown : StreamUiEvent()
}