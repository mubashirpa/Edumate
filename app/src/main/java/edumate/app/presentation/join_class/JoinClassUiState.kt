package edumate.app.presentation.join_class

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText
import edumate.app.presentation.class_details.UserType

data class JoinClassUiState(
    val classCode: String = "",
    val classCodeError: UiText? = null,
    val currentUser: FirebaseUser? = null,
    val openProgressDialog: Boolean = false,
    val openUserTypeBottomSheet: Boolean = false,
    val userMessage: UiText? = null,
    val userType: UserType = UserType.STUDENT
)