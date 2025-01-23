package app.edumate.presentation.newPassword

sealed class NewPasswordUiEvent {
    data object UpdatePassword : NewPasswordUiEvent()

    data object UserMessageShown : NewPasswordUiEvent()
}
