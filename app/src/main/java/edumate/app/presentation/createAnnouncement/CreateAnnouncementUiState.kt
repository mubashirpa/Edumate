package edumate.app.presentation.createAnnouncement

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.Material

data class CreateAnnouncementUiState(
    val attachments: SnapshotStateList<Material> = mutableStateListOf(),
    val isCreateAnnouncementSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val openAddLinkDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val showAddAttachmentBottomSheet: Boolean = false,
    val text: String = "",
    val textError: UiText? = null,
    val userMessage: UiText? = null,
)
