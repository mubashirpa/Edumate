package app.edumate.presentation.people

import app.edumate.domain.model.users.Users

sealed class PeopleUiEvent {
    data class OnAppBarDropdownExpandedChange(
        val expanded: Boolean,
    ) : PeopleUiEvent()

    data class OnDeletePerson(
        val userId: String,
    ) : PeopleUiEvent()

    data class OnFilterChange(
        val type: PeopleFilterType,
    ) : PeopleUiEvent()

    data class OnLeaveClass(
        val userId: String,
    ) : PeopleUiEvent()

    data class OnOpenDeleteUserDialogChange(
        val user: Users?,
    ) : PeopleUiEvent()

    data class OnOpenLeaveClassDialogChange(
        val open: Boolean,
    ) : PeopleUiEvent()

    data class OnShowInviteBottomSheetChange(
        val show: Boolean,
    ) : PeopleUiEvent()

    data object OnRefresh : PeopleUiEvent()

    data object OnRetry : PeopleUiEvent()

    data object UserMessageShown : PeopleUiEvent()
}
