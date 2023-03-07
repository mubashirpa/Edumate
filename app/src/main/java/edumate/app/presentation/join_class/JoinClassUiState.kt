package edumate.app.presentation.join_class

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText

data class JoinClassUiState(
    val currentUser: FirebaseUser? = null,
    val classCode: String = "",
    val classCodeError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null
)