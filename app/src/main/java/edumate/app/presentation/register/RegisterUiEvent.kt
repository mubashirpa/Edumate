package edumate.app.presentation.register

sealed class RegisterUiEvent {
    data class OnEmailValueChange(val email: String) : RegisterUiEvent()

    data class OnNameValueChange(val name: String) : RegisterUiEvent()

    data class OnPasswordValueChange(val password: String) : RegisterUiEvent()

    data class SignInWithGoogle(val token: String) : RegisterUiEvent()

    data object SignUp : RegisterUiEvent()

    data object UserMessageShown : RegisterUiEvent()
}
