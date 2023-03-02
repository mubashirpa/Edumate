package edumate.app.presentation.create_room

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText

data class CreateRoomUiState(
    val currentUser: FirebaseUser? = null,
    val name: String = "",
    val section: String = "",
    val subject: String = "",
    val openProgressDialog: Boolean = false,
    val nameError: UiText? = null,
    val userMessage: UiText? = null
)