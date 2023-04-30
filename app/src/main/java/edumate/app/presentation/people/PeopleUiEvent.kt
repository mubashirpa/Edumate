package edumate.app.presentation.people

import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType

sealed class PeopleUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : PeopleUiEvent()
    data class OnFilterChange(val peopleFilterType: PeopleFilterType) : PeopleUiEvent()
    data class OnLeaveClass(val uid: String) : PeopleUiEvent()
    data class OnOpenFabMenuChange(val open: Boolean) : PeopleUiEvent()
    data class OnOpenLeaveClassDialogChange(val open: Boolean) : PeopleUiEvent()
    data class OnOpenRemoveUserDialogChange(val userProfile: UserProfile?) : PeopleUiEvent()
    data class OnRemoveUser(val userType: UserType, val uid: String) : PeopleUiEvent()
    object OnRefresh : PeopleUiEvent()
    object OnRetry : PeopleUiEvent()
    object UserMessageShown : PeopleUiEvent()
}