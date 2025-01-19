package app.edumate.presentation.profile

import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.User

data class ProfileUiState(
    val currentUserResult: Result<User> = Result.Empty(),
    val isUserSignOut: Boolean = false,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
