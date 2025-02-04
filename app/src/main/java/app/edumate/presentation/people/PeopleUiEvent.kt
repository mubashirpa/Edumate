package app.edumate.presentation.people

import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User

sealed class PeopleUiEvent {
    data class ChangePersonRole(
        val userId: String,
        val role: UserRole,
    ) : PeopleUiEvent()

    data class DeletePerson(
        val userId: String,
    ) : PeopleUiEvent()

    data class LeaveClass(
        val userId: String,
    ) : PeopleUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : PeopleUiEvent()

    data class OnFilterValueChange(
        val type: PeopleFilterType,
    ) : PeopleUiEvent()

    data class OnOpenDeleteUserDialogChange(
        val user: User?,
    ) : PeopleUiEvent()

    data class OnOpenLeaveClassDialogChange(
        val open: Boolean,
    ) : PeopleUiEvent()

    data class OnShowInviteBottomSheetChange(
        val show: Boolean,
    ) : PeopleUiEvent()

    data object Refresh : PeopleUiEvent()

    data object Retry : PeopleUiEvent()

    data object UserMessageShown : PeopleUiEvent()
}
