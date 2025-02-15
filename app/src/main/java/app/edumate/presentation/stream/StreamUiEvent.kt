package app.edumate.presentation.stream

import app.edumate.domain.model.announcement.Announcement
import java.io.File

sealed class StreamUiEvent {
    data class AddLinkAttachment(
        val link: String,
    ) : StreamUiEvent()

    data class DeleteAnnouncement(
        val announcementId: String,
    ) : StreamUiEvent()

    data class OnEditAnnouncement(
        val announcement: Announcement?,
    ) : StreamUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : StreamUiEvent()

    data class OnFilePicked(
        val file: File,
        val title: String,
        val mimeType: String?,
        val size: Long?,
    ) : StreamUiEvent()

    data class OnOpenAddLinkDialogChange(
        val open: Boolean,
    ) : StreamUiEvent()

    data class OnOpenDeleteAnnouncementDialogChange(
        val announcementId: String?,
    ) : StreamUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(
        val show: Boolean,
    ) : StreamUiEvent()

    data class OnShowCommentsBottomSheetChange(
        val announcementId: String?,
    ) : StreamUiEvent()

    data class RemoveAttachment(
        val position: Int,
    ) : StreamUiEvent()

    data class SetAnnouncementPinned(
        val announcementId: String,
        val pinned: Boolean,
    ) : StreamUiEvent()

    data object CreateAnnouncement : StreamUiEvent()

    data object Refresh : StreamUiEvent()

    data object Retry : StreamUiEvent()

    data object UserMessageShown : StreamUiEvent()
}
