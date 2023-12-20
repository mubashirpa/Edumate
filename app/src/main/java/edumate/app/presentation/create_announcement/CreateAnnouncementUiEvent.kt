package edumate.app.presentation.create_announcement

import android.net.Uri
import edumate.app.core.utils.FileUtils

sealed class CreateAnnouncementUiEvent {
    data class OnAddLinkAttachment(val link: String) : CreateAnnouncementUiEvent()
    data class OnFilePicked(val uri: Uri, val fileUtils: FileUtils) : CreateAnnouncementUiEvent()
    data class OnOpenAddLinkDialogChange(val open: Boolean) : CreateAnnouncementUiEvent()
    data class OnOpenAttachmentMenuChange(val open: Boolean) : CreateAnnouncementUiEvent()
    data class OnRemoveAttachment(val position: Int) : CreateAnnouncementUiEvent()
    data class OnTextChange(val text: String) : CreateAnnouncementUiEvent()
    data object PostAnnouncement : CreateAnnouncementUiEvent()
    data object UserMessageShown : CreateAnnouncementUiEvent()
}