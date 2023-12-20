package edumate.app.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.authentication.SignOutUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val signOutUseCase: SignOutUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(ProfileUiState())
            private set

        init {
            getCurrentUserUseCase().map { user ->
                uiState = uiState.copy(currentUser = user)
            }.launchIn(viewModelScope)
        }

        fun onEvent(event: ProfileUiEvent) {
            when (event) {
                ProfileUiEvent.SignOut -> {
                    signOutUseCase()
                }
            }
        }
    }
