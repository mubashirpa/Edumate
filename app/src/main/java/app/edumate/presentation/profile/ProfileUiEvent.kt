package app.edumate.presentation.profile

sealed class ProfileUiEvent {
    data object SignOut : ProfileUiEvent()
}
