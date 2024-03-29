package edumate.app.presentation.register

sealed class RegisterUiEvent {
    data class EmailChanged(val email: String) : RegisterUiEvent()
    data class NameChanged(val name: String) : RegisterUiEvent()
    data class OnGoogleSignUpClick(val token: String) : RegisterUiEvent()
    data class PasswordChanged(val password: String) : RegisterUiEvent()
    object OnSignUpClick : RegisterUiEvent()
    object UserMessageShown : RegisterUiEvent()
}