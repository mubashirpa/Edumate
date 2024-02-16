package edumate.app.presentation.createAnnouncement

import android.net.Uri
import edumate.app.core.utils.FileUtils

sealed class CreateAnnouncementUiEvent {
    data class OnAddLinkAttachment(val link: String) : CreateAnnouncementUiEvent()

    data class OnFilePicked(val uri: Uri, val fileUtils: FileUtils) : CreateAnnouncementUiEvent()

    data class OnOpenAddLinkDialogChange(val openDialog: Boolean) : CreateAnnouncementUiEvent()

    data class OnRemoveAttachment(val position: Int) : CreateAnnouncementUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(val showBottomSheet: Boolean) :
        CreateAnnouncementUiEvent()

    data class OnTextValueChange(val text: String) : CreateAnnouncementUiEvent()

    data object PostAnnouncement : CreateAnnouncementUiEvent()

    data object UserMessageShown : CreateAnnouncementUiEvent()
}
