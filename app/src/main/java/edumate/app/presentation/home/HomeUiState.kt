package edumate.app.presentation.home

import edumate.app.domain.model.userProfiles.UserProfile

data class HomeUiState(
    val appBarDropdownExpanded: Boolean = false,
    val currentUser: UserProfile? = null,
    val isRefreshing: Boolean = false,
    val showAddCourseBottomSheet: Boolean = false,
)
