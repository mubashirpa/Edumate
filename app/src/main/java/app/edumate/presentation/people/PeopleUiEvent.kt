package app.edumate.presentation.people

import app.edumate.domain.model.user.Users

sealed class PeopleUiEvent {
    data class OnDeletePerson(
        val userId: String,
    ) : PeopleUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
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
