package app.edumate.presentation.signUp

sealed class SignUpUiEvent {
    data class SignUpWithGoogle(
        val token: String,
        val nonce: String,
    ) : SignUpUiEvent()

    data object SignUp : SignUpUiEvent()

    data object UserMessageShown : SignUpUiEvent()
}
