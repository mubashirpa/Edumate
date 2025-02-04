package app.edumate.presentation.people

import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.user.User

data class PeopleUiState(
    val currentUserId: String? = null,
    val expandedAppBarDropdown: Boolean = false,
    val deletePerson: User? = null,
    val filter: PeopleFilterType = PeopleFilterType.ALL,
    val isRefreshing: Boolean = false,
    val isUserLeftCourse: Boolean = false,
    val openLeaveClassDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val peopleResult: Result<List<User>> = Result.Empty(),
    val showInviteBottomSheet: Boolean = false,
    val students: List<User> = emptyList(),
    val teachers: List<User> = emptyList(),
    val userMessage: UiText? = null,
)
