package edumate.app.presentation.people

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.user_profiles.UserProfile

data class PeopleUiState(
    val appBarMenuExpanded: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val filter: PeopleFilterType = PeopleFilterType.ALL,
    val isUserLeaveClass: Boolean = false,
    val openFabMenu: Boolean = false,
    val openLeaveClassDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val peoples: List<UserProfile> = emptyList(),
    val refreshing: Boolean = false,
    val removeUserProfile: UserProfile? = null,
    val userMessage: UiText? = null,
)
