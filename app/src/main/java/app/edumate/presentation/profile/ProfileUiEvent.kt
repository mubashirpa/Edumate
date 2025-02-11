package app.edumate.presentation.profile

sealed class ProfileUiEvent {
    data class OnOpenLogoutDialogChange(
        val open: Boolean,
    ) : ProfileUiEvent()

    data object SignOut : ProfileUiEvent()
}
