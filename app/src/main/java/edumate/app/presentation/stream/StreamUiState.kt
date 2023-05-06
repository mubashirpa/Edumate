package edumate.app.presentation.stream

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.announcements.Announcement

data class StreamUiState(
    val appBarMenuExpanded: Boolean = false,
    val announcements: List<Announcement> = emptyList(),
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val deleteAnnouncementId: String? = null,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val userMessage: UiText? = null
)