package edumate.app.presentation.people

import edumate.app.domain.model.userProfiles.UserProfile

sealed class PeopleUiEvent {
    data class OnAppBarDropdownExpandedChange(val expanded: Boolean) : PeopleUiEvent()

    data class OnDeleteStudent(val userId: String) : PeopleUiEvent()

    data class OnDeleteTeacher(val userId: String) : PeopleUiEvent()

    data class OnFilterChange(val peopleFilterType: PeopleFilterType) : PeopleUiEvent()

    data class OnLeaveClass(val userId: String) : PeopleUiEvent()

    data class OnOpenDeleteUserDialogChange(val userProfile: UserProfile?) : PeopleUiEvent()

    data class OnOpenLeaveClassDialogChange(val open: Boolean) : PeopleUiEvent()

    data class OnShowInviteBottomSheetChange(val show: Boolean) : PeopleUiEvent()

    data object OnRefresh : PeopleUiEvent()

    data object OnRetry : PeopleUiEvent()

    data object UserMessageShown : PeopleUiEvent()
}
