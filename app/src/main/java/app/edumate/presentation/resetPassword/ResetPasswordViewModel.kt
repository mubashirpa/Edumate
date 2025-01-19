package app.edumate.presentation.resetPassword

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.usecase.authentication.ResetPasswordUseCase
import app.edumate.domain.usecase.validation.ValidateEmail
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ResetPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val validateEmail: ValidateEmail,
) : ViewModel() {
    var uiState by mutableStateOf(ResetPasswordUiState())
        private set

    private val resetPassword = savedStateHandle.toRoute<Screen.ResetPassword>()

    init {
        resetPassword.email?.let {
            uiState.email.setTextAndPlaceCursorAtEnd(it)
        }
    }

    fun onEvent(event: ResetPasswordUiEvent) {
        when (event) {
            ResetPasswordUiEvent.ResetPassword -> {
                resetPassword(uiState.email.text.toString())
            }

            ResetPasswordUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun resetPassword(email: String) {
        val emailResult = validateEmail.execute(email)
        uiState = uiState.copy(emailError = emailResult.error)

        if (!emailResult.successful) return

        resetPasswordUseCase(email = email)
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
                            uiState.copy(openProgressDialog = true)
                        }

                        is Result.Success -> {
                            uiState.copy(
                                isResetPassword = true,
                                openProgressDialog = false,
                            )
                        }
                    }
            }.launchIn(viewModelScope)
    }
}
