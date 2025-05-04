package app.edumate.presentation.main

import com.google.android.play.core.appupdate.AppUpdateInfo

data class MainUiState(
    val isLoading: Boolean = true,
    val isUserLoggedIn: Boolean = false,
    val notificationPermissionRequested: Boolean = false,
    val openRequestNotificationPermissionDialog: Boolean = false,
    val updateAvailable: Boolean = false,
    val updateInfo: AppUpdateInfo? = null,
)
