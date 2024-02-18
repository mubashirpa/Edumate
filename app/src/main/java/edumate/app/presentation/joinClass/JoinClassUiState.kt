package edumate.app.presentation.joinClass

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText
import edumate.app.presentation.classDetails.UserType

data class JoinClassUiState(
    val classCode: String = "",
    val classCodeError: UiText? = null,
    val currentUser: FirebaseUser? = null,
    val openProgressDialog: Boolean = false,
    val showUserTypeBottomSheet: Boolean = false,
    val userMessage: UiText? = null,
    val userType: UserType = UserType.STUDENT,
)
