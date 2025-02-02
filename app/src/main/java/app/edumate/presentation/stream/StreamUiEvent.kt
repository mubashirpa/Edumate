package app.edumate.presentation.stream

sealed class StreamUiEvent {
    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : StreamUiEvent()

    data object OnRefresh : StreamUiEvent()

    data object OnRetry : StreamUiEvent()

    data object UserMessageShown : StreamUiEvent()
}
