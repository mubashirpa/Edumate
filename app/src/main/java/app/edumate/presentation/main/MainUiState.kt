package app.edumate.presentation.main

data class MainUiState(
    val isLoading: Boolean = true,
    val isUserLoggedIn: Boolean = false,
    val notificationPermissionRequested: Boolean = false,
    val openRequestNotificationPermissionDialog: Boolean = false,
)
