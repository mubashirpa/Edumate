package app.edumate.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.usecase.CheckUpdateUseCase
import app.edumate.domain.usecase.authentication.IsUserLoggedInUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val checkUpdateUseCase: CheckUpdateUseCase,
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
        uiState =
            when (event) {
                is MainUiEvent.OnNotificationPermissionRequestedChange -> {
                    uiState.copy(notificationPermissionRequested = event.requested)
                }

                is MainUiEvent.OnOpenRequestNotificationPermissionDialogChange -> {
                    uiState.copy(openRequestNotificationPermissionDialog = event.open)
                }
            }
    }

    private fun checkUpdate() {
        checkUpdateUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    val updateInfo = result.data!!
                    uiState =
                        uiState.copy(
                            updateAvailable = true,
                            updateInfo = updateInfo,
                        )
                }
            }.launchIn(viewModelScope)
    }
}
