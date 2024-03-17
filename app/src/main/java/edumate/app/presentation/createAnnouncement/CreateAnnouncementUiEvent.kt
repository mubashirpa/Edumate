package edumate.app.presentation.createAnnouncement

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue

sealed class CreateAnnouncementUiEvent {
    data class OnAddLinkAttachment(val link: String) : CreateAnnouncementUiEvent()

    data class OnFilePicked(val uri: Uri, val title: String) : CreateAnnouncementUiEvent()

    data class OnOpenAddLinkDialogChange(val open: Boolean) : CreateAnnouncementUiEvent()

    data class OnRemoveAttachment(val position: Int) : CreateAnnouncementUiEvent()

    data class OnShowAddAttachmentBottomSheetChange(val show: Boolean) : CreateAnnouncementUiEvent()

    data class OnTextValueChange(val text: TextFieldValue) : CreateAnnouncementUiEvent()

    data object PostAnnouncement : CreateAnnouncementUiEvent()

    data object UserMessageShown : CreateAnnouncementUiEvent()
}
