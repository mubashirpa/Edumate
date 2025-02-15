package app.edumate.presentation.stream

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.model.material.Material

data class StreamUiState(
    val announcementResult: Result<List<Announcement>> = Result.Empty(),
    val announcements: List<Announcement> = emptyList(),
    val attachments: SnapshotStateList<Material> = mutableStateListOf(),
    val currentUserId: String? = null,
    val deleteAnnouncementId: String? = null,
    val editAnnouncement: Announcement? = null,
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val openAddLinkDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val replyAnnouncementId: String? = null,
    val showAddAttachmentBottomSheet: Boolean = false,
    val text: TextFieldState = TextFieldState(),
    val uploadProgress: Float? = null,
    val userMessage: UiText? = null,
)
