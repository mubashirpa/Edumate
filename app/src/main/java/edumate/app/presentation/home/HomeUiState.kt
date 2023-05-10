package edumate.app.presentation.home

import com.google.firebase.auth.FirebaseUser

data class HomeUiState(
    val appBarMenuExpanded: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val openFabMenu: Boolean = false,
    val refreshing: Boolean = false
)