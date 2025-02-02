package app.edumate.presentation.stream

sealed class StreamUiEvent {
    data class OnDeleteAnnouncement(
        val id: String,
    ) : StreamUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : StreamUiEvent()

    data class OnOpenDeleteAnnouncementDialogChange(
        val announcementId: String?,
    ) : StreamUiEvent()

    data object CreateAnnouncement : StreamUiEvent()

    data object OnRefresh : StreamUiEvent()

    data object OnRetry : StreamUiEvent()

    data object UserMessageShown : StreamUiEvent()
}
