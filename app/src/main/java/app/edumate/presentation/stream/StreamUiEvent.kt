package app.edumate.presentation.stream

import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.model.comment.Comment
import java.io.File

sealed class StreamUiEvent {
    data class AddComment(
        val announcementId: String,
    ) : StreamUiEvent()

    data class AddLinkAttachment(
        val link: String,
    ) : StreamUiEvent()

    data class DeleteAnnouncement(
        val id: String,
    ) : StreamUiEvent()

    data class DeleteComment(
        val announcementId: String,
        val id: String,
    ) : StreamUiEvent()

    data class OnEditAnnouncement(
        val announcement: Announcement?,
    ) : StreamUiEvent()

    data class OnEditComment(
        val announcementId: String,
        val comment: Comment?,
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

    data class OnOpenDeleteCommentDialogChange(
        val commentId: String?,
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

    data class RetryComment(
        val announcementId: String,
    ) : StreamUiEvent()

    data object CreateAnnouncement : StreamUiEvent()

    data object Refresh : StreamUiEvent()

    data object Retry : StreamUiEvent()

    data object UserMessageShown : StreamUiEvent()
}
