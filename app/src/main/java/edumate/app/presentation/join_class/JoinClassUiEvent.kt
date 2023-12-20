package edumate.app.presentation.join_class

import edumate.app.presentation.class_details.UserType

sealed class JoinClassUiEvent {
    data class OnClassCodeChange(val classCode: String) : JoinClassUiEvent()

    data class OnOpenUserTypeBottomSheetChange(val open: Boolean) : JoinClassUiEvent()

    data class OnUserTypeChange(val userType: UserType) : JoinClassUiEvent()

    data object JoinClass : JoinClassUiEvent()

    data object UserMessageShown : JoinClassUiEvent()
}
