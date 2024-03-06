package edumate.app.presentation.profile

import edumate.app.domain.model.userProfiles.UserProfile

data class ProfileUiState(
    val currentUser: UserProfile? = null,
)
