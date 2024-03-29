package edumate.app.presentation.login

sealed class LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent()
    data class OnGoogleSignInClick(val token: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    object OnSignInClick : LoginUiEvent()
    object UserMessageShown : LoginUiEvent()
}