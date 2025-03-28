package app.edumate.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.authentication.SignOutUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        getCurrentUserUseCase()
            .onEach { result ->
                uiState = uiState.copy(currentUserResult = result)
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.OnOpenLogoutDialogChange -> {
                uiState = uiState.copy(openLogoutDialog = event.open)
            }

            ProfileUiEvent.SignOut -> {
                signOut()
            }
        }
    }

    private fun signOut() {
        signOutUseCase()
            .onEach { result ->
                uiState =
                    when (result) {
                        is Result.Empty -> {
                            uiState
                        }

                        is Result.Error -> {
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                        }

                        is Result.Loading -> {
                            uiState.copy(
                                openLogoutDialog = false,
                                openProgressDialog = true,
                            )
                        }

                        is Result.Success -> {
                            uiState.copy(
                                isUserSignOut = true,
                                openProgressDialog = false,
                            )
                        }
                    }
            }.launchIn(viewModelScope)
    }
}
