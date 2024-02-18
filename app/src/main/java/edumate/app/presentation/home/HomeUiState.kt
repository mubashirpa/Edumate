package edumate.app.presentation.home

import com.google.firebase.auth.FirebaseUser

data class HomeUiState(
    val appBarDropdownExpanded: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val isRefreshing: Boolean = false,
    val showAddCourseBottomSheet: Boolean = false,
)
