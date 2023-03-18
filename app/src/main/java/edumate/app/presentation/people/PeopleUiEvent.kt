package edumate.app.presentation.people

import edumate.app.presentation.class_details.UserType

sealed class PeopleUiEvent {
    data class OnDeletePeople(val userType: UserType, val uid: String) : PeopleUiEvent()
    data class OnFilterChange(val peopleFilter: PeopleFilter) : PeopleUiEvent()
    data class OnLeaveClass(val uid: String) : PeopleUiEvent()
    data class OnOpenFabMenuChange(val open: Boolean) : PeopleUiEvent()
    data class OnOpenLeaveClassDialogChange(val open: Boolean) : PeopleUiEvent()
    object OnRefresh : PeopleUiEvent()
    object OnRetry : PeopleUiEvent()
    object UserMessageShown : PeopleUiEvent()
}