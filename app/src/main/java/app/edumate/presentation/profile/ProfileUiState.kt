package app.edumate.presentation.profile

import app.edumate.core.UiText
import app.edumate.domain.model.User

data class ProfileUiState(
    val currentUser: User? = null,
    val isUserSignOut: Boolean = false,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
