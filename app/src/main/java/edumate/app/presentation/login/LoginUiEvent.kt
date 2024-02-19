package edumate.app.presentation.login

sealed class LoginUiEvent {
    data class OnEmailValueChange(val email: String) : LoginUiEvent()

    data class OnPasswordValueChange(val password: String) : LoginUiEvent()

    data class SignInWithGoogle(val token: String) : LoginUiEvent()

    data object SignIn : LoginUiEvent()

    data object UserMessageShown : LoginUiEvent()
}
