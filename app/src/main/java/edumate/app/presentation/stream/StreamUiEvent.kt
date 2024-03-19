package edumate.app.presentation.stream

sealed class StreamUiEvent {
    data class OnAppBarDropdownExpandedChange(val expanded: Boolean) : StreamUiEvent()

    data class OnDeleteAnnouncement(val id: String) : StreamUiEvent()

    data class OnOpenDeleteAnnouncementDialogChange(val id: String?) : StreamUiEvent()

    data object OnRefresh : StreamUiEvent()

    data object OnRetry : StreamUiEvent()

    data object UserMessageShown : StreamUiEvent()
}
