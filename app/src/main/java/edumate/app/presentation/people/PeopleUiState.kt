package edumate.app.presentation.people

import com.google.firebase.auth.FirebaseUser
import edumate.app.R.string as Strings
import edumate.app.core.UiText
import edumate.app.domain.model.User

data class PeopleUiState(
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val filter: PeopleFilterType = PeopleFilterType.ALL,
    val isFabExpanded: Boolean = false,
    val isUserLeaveClass: Boolean = false,
    val openFabMenu: Boolean = false,
    val openLeaveClassDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val peoples: List<User> = emptyList(),
    val progressDialogText: UiText = UiText.StringResource(Strings.loading),
    val refreshing: Boolean = false,
    val userMessage: UiText? = null
)