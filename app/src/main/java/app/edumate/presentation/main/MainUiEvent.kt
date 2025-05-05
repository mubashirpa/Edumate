package app.edumate.presentation.main

sealed class MainUiEvent {
    data class OnNotificationPermissionRequestedChange(
        val requested: Boolean,
    ) : MainUiEvent()

    data class OnOpenRequestNotificationPermissionDialogChange(
        val open: Boolean,
    ) : MainUiEvent()

    data object OnResume : MainUiEvent()
}
