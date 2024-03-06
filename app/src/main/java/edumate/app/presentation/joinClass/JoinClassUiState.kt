package edumate.app.presentation.joinClass

import edumate.app.core.UiText
import edumate.app.domain.model.userProfiles.UserProfile

data class JoinClassUiState(
    val classCode: String = "",
    val classCodeError: UiText? = null,
    val currentUser: UserProfile? = null,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
