package edumate.app.presentation.create_announcement

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import edumate.app.core.UiText
import edumate.app.domain.model.announcements.Material

data class CreateAnnouncementUiState(
    val attachments: SnapshotStateList<Material> = mutableStateListOf(),
    val openAddLinkDialog: Boolean = false,
    val openAttachmentMenu: Boolean = false,
    val openProgressDialog: Boolean = false,
    val text: String = "",
    val textError: UiText? = null
)