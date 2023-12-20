package edumate.app.presentation.profile

sealed class ProfileUiEvent {
    data object SignOut : ProfileUiEvent()
}
