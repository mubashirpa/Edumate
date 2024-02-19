package edumate.app.presentation.recover

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.SendPasswordResetEmailUseCase
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.navigation.Routes
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RecoverViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
        private val validateEmail: ValidateEmail,
    ) : ViewModel() {
        var uiState by mutableStateOf(RecoverUiState())
            private set

        private val email: String? = savedStateHandle[Routes.Args.RECOVER_EMAIL]

        init {
            uiState = uiState.copy(email = email.orEmpty())
        }

        fun onEvent(event: RecoverUiEvent) {
            when (event) {
                is RecoverUiEvent.OnEmailValueChange -> {
                    uiState =
                        uiState.copy(
                            email = event.email,
                            emailError = null,
                        )
                }

                is RecoverUiEvent.Recover -> {
                    sendPasswordResetEmail(uiState.email.trim())
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

            sendPasswordResetEmailUseCase(email).onEach { result ->
                uiState =
                    when (result) {
                        is Result.Empty -> {
                            uiState
                        }

                        is Result.Error -> {
                            Log.d("hello", result.message.toString())
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                        }

                        is Result.Loading -> {
                            uiState.copy(openProgressDialog = true)
                        }

                        is Result.Success -> {
                            uiState.copy(
                                isPasswordResetEmailSend = true,
                                openProgressDialog = false,
                            )
                        }
                    }
            }.launchIn(viewModelScope)
        }
    }
