package edumate.app.presentation.profile

import com.google.firebase.auth.FirebaseUser

data class ProfileUiState(
    val currentUser: FirebaseUser? = null,
)
