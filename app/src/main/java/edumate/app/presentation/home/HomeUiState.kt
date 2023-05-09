package edumate.app.presentation.home

import com.google.firebase.auth.FirebaseUser

data class HomeUiState(
    val currentUser: FirebaseUser? = null,
    val openFabMenu: Boolean = false
)