package edumate.app.presentation.stream

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.announcements.Announcement
import edumate.app.domain.model.userProfiles.UserProfile

data class StreamUiState(
    val announcementsResult: Result<List<Announcement>> = Result.Empty(),
    val appBarDropdownExpanded: Boolean = false,
    val deleteAnnouncementId: String? = null,
    val isRefreshing: Boolean = false,
    val openProgressDialog: Boolean = false,
    val user: UserProfile? = null,
    val userMessage: UiText? = null,
)
