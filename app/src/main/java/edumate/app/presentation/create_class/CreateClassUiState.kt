package edumate.app.presentation.create_class

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText

data class CreateClassUiState(
    val currentUser: FirebaseUser? = null,
    val error: UiText? = null,
    val isFabExpanded: Boolean = false,
    val loading: Boolean = false,
    val name: String = "",
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val progressDialogText: UiText = UiText.Empty,
    val room: String = "",
    val section: String = "",
    val subject: String = "",
    val userMessage: UiText? = null
)