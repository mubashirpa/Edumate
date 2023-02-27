package edumate.app.presentation.create_room

import com.google.firebase.auth.FirebaseUser

data class CreateRoomUiState(
    val currentUser: FirebaseUser? = null,
    val name: String = "",
    val section: String = "",
    val subject: String = ""
)