package edumate.app.presentation.people

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.User

data class PeopleUiState(
    val appBarMenuExpanded: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val filter: PeopleFilterType = PeopleFilterType.ALL,
    val isFabExpanded: Boolean = false,
    val isUserLeaveClass: Boolean = false,
    val openFabMenu: Boolean = false,
    val openLeaveClassDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val peoples: List<User> = emptyList(),
    val refreshing: Boolean = false,
    val removeUser: User? = null,
    val userMessage: UiText? = null
)