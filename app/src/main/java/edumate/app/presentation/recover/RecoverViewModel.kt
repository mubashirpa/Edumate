package edumate.app.presentation.recover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.authentication.SendPasswordResetEmailUseCase
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class RecoverViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val validateEmail: ValidateEmail
) : ViewModel() {

    var uiState by mutableStateOf(RecoverUiState())
        private set

    private val email: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.RECOVER_EMAIL])
    } catch (e: IllegalStateException) {
        null
    }

    init {
        uiState = uiState.copy(email = email.orEmpty())
    }

    fun onEvent(event: RecoverUiEvent) {
        when (event) {
            is RecoverUiEvent.EmailChanged -> {
                uiState = uiState.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is RecoverUiEvent.OnRecoverClick -> {
                sendPasswordResetEmail(uiState.email)
            }
            is RecoverUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        val emailResult = validateEmail.execute(email)
        uiState = uiState.copy(emailError = emailResult.error)

        if (!emailResult.successful) return

        sendPasswordResetEmailUseCase(email).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    uiState.copy(
                        openProgressDialog = false,
                        isPasswordResetEmailSend = true
                    )
                }
                is Resource.Error -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}