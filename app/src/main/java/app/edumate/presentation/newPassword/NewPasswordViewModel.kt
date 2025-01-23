package app.edumate.presentation.newPassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.usecase.authentication.UpdatePasswordUseCase
import app.edumate.domain.usecase.validation.ValidatePassword
import app.edumate.domain.usecase.validation.ValidateRepeatedPassword
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NewPasswordViewModel(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword,
) : ViewModel() {
    var uiState by mutableStateOf(NewPasswordUiState())
        private set

    fun onEvent(event: NewPasswordUiEvent) {
        when (event) {
            NewPasswordUiEvent.UpdatePassword -> {
                updatePassword(
                    password =
                        uiState.password.text
                            .toString()
                            .trim(),
                    repeatedPassword =
                        uiState.repeatedPassword.text
                            .toString()
                            .trim(),
                )
            }

            NewPasswordUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    fun updatePassword(
        password: String,
        repeatedPassword: String,
    ) {
        val passwordResult = validatePassword.execute(password)
        val repeatedPasswordResult =
            validateRepeatedPassword.execute(
                password = password,
                repeatedPassword = repeatedPassword,
            )

        uiState =
            uiState.copy(
                passwordError = passwordResult.error,
                repeatedPasswordError = repeatedPasswordResult.error,
            )

        val hasError =
            listOf(
                passwordResult,
                repeatedPasswordResult,
            ).any { !it.successful }

        if (hasError) return

        updatePasswordUseCase(password)
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
                                isUpdatePassword = true,
                                openProgressDialog = false,
                            )
                        }
                    }
            }.launchIn(viewModelScope)
    }
}
