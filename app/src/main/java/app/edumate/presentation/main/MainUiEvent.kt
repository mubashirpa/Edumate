package app.edumate.presentation.main

sealed class MainUiEvent {
    data class OnOpenRequestNotificationPermissionDialogChange(
        val open: Boolean,
    ) : MainUiEvent()
}
