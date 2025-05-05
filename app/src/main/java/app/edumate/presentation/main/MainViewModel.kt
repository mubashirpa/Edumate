package app.edumate.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.domain.usecase.authentication.IsUserLoggedInUseCase
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val updateManager: AppUpdateManager,
) : ViewModel() {
    var uiState by mutableStateOf(MainUiState())
        private set

    init {
        viewModelScope.launch {
            uiState =
                uiState.copy(
                    isUserLoggedIn = isUserLoggedInUseCase(),
                    isLoading = false,
                )
        }
        checkUpdate()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnNotificationPermissionRequestedChange -> {
                uiState = uiState.copy(notificationPermissionRequested = event.requested)
            }

            is MainUiEvent.OnOpenRequestNotificationPermissionDialogChange -> {
                uiState = uiState.copy(openRequestNotificationPermissionDialog = event.open)
            }

            MainUiEvent.OnResume -> {
                resumeUpdate()
            }
        }
    }

    private fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val updateInfoTask = updateManager.appUpdateInfo
            updateInfoTask.addOnSuccessListener { updateInfo ->
                if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    uiState =
                        uiState.copy(
                            updateAvailable = true,
                            updateInfo = updateInfo,
                        )
                }
            }
        }
    }

    private fun resumeUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val updateInfoTask = updateManager.appUpdateInfo
            updateInfoTask.addOnSuccessListener { updateInfo ->
                if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    uiState =
                        uiState.copy(
                            updateAvailable = true,
                            updateInfo = updateInfo,
                        )
                }
            }
        }
    }
}
