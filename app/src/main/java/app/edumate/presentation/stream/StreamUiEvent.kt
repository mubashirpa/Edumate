package app.edumate.presentation.stream

import app.edumate.domain.model.announcement.Announcement
import java.io.File

sealed class StreamUiEvent {
    data class OnAddLinkAttachment(
        val link: String,
    ) : StreamUiEvent()

    data class OnDeleteAnnouncement(
        val id: String,
    ) : StreamUiEvent()

    data class OnEditAnnouncement(
        val index: Int?,
        val announcement: Announcement?,
    ) : StreamUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : StreamUiEvent()

    data class OnFilePicked(
        val file: File,
        val title: String,
    ) : StreamUiEvent()

    data class OnOpenAddLinkDialogChange(
        val open: Boolean,
    ) : StreamUiEvent()

    data class OnOpenDeleteAnnouncementDialogChange(
        val announcementId: String?,
    ) : StreamUiEvent()

    data class OnRemoveAttachment(
        val position: Int,
    ) : StreamUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(
        val show: Boolean,
    ) : StreamUiEvent()

    data object CreateAnnouncement : StreamUiEvent()

    data object OnRefresh : StreamUiEvent()

    data object OnRetry : StreamUiEvent()

    data object UserMessageShown : StreamUiEvent()
}
