package app.edumate.presentation.signIn

sealed class SignInUiEvent {
    data class OnRememberSwitchCheckedChange(
        val checked: Boolean,
    ) : SignInUiEvent()

    data class OnShowVerifyEmailBottomSheetChange(
        val show: Boolean,
    ) : SignInUiEvent()

    data class SignInWithGoogle(
        val token: String,
        val nonce: String,
    ) : SignInUiEvent()

    data object ResendVerifyEmail : SignInUiEvent()

    data object SignIn : SignInUiEvent()

    data object UserMessageShown : SignInUiEvent()
}
