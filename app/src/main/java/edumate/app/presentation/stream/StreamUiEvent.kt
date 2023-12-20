package edumate.app.presentation.stream

sealed class StreamUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : StreamUiEvent()
    data class OnDeleteAnnouncement(val announcementId: String) : StreamUiEvent()
    data class OnOpenDeleteAnnouncementDialogChange(val announcementId: String?) : StreamUiEvent()
    data object OnRefresh : StreamUiEvent()
    data object OnRetry : StreamUiEvent()
    data object UserMessageShown : StreamUiEvent()
}