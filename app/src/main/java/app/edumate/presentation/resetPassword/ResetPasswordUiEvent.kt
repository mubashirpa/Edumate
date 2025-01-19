package app.edumate.presentation.resetPassword

sealed class ResetPasswordUiEvent {
    data object ResetPassword : ResetPasswordUiEvent()

    data object UserMessageShown : ResetPasswordUiEvent()
}
