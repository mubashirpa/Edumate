package edumate.app.presentation.create_class

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText

data class CreateClassUiState(
    val currentUser: FirebaseUser? = null,
    val name: String = "",
    val section: String = "",
    val room: String = "",
    val subject: String = "",
    val openProgressDialog: Boolean = false,
    val nameError: UiText? = null,
    val userMessage: UiText? = null,
    val isFabExpanded: Boolean = false
)