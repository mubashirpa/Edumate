package edumate.app.presentation.joinClass

import edumate.app.presentation.classDetails.UserType

sealed class JoinClassUiEvent {
    data class OnClassCodeValueChange(val classCode: String) : JoinClassUiEvent()

    data class OnShowUserTypeBottomSheetChange(val showBottomSheet: Boolean) : JoinClassUiEvent()

    data class OnUserTypeChange(val userType: UserType) : JoinClassUiEvent()

    data object JoinClass : JoinClassUiEvent()

    data object UserMessageShown : JoinClassUiEvent()
}
