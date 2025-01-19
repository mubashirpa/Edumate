package app.edumate.presentation.signUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.usecase.authentication.SignInWithGoogleUseCase
import app.edumate.domain.usecase.authentication.SignUpUseCase
import app.edumate.domain.usecase.validation.ValidateEmail
import app.edumate.domain.usecase.validation.ValidateName
import app.edumate.domain.usecase.validation.ValidatePassword
import app.edumate.domain.usecase.validation.ValidateRepeatedPassword
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword,
) : ViewModel() {
    var uiState by mutableStateOf(SignUpUiState())
        private set

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            SignUpUiEvent.SignUp -> {
                signUp(
                    name = uiState.name.text.toString(),
                    email = uiState.email.text.toString(),
                    password = uiState.password.text.toString(),
                    repeatedPassword = uiState.repeatedPassword.text.toString(),
                )
            }

            is SignUpUiEvent.SignUpWithGoogle -> {
                signUpWithGoogle(token = event.token, nonce = event.nonce)
            }

            SignUpUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun signUp(
        name: String,
        email: String,
        password: String,
        repeatedPassword: String,
    ) {
        val nameResult = validateName.execute(name)
        val emailResult = validateEmail.execute(email)
        val passwordResult = validatePassword.execute(password)
        val repeatedPasswordResult =
            validateRepeatedPassword.execute(
                password = password,
                repeatedPassword = repeatedPassword,
            )

        uiState =
            uiState.copy(
                nameError = nameResult.error,
                emailError = emailResult.error,
                passwordError = passwordResult.error,
                repeatedPasswordError = repeatedPasswordResult.error,
            )

        val hasError =
            listOf(
                nameResult,
                emailResult,
                passwordResult,
                repeatedPasswordResult,
            ).any { !it.successful }

        if (hasError) return

        signUpUseCase(
            name = name,
            email = email,
            password = password,
        ).onEach { result ->
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
                            isUserLoggedIn = true,
                            openProgressDialog = false,
                        )
                    }
                }
        }.launchIn(viewModelScope)
    }

    private fun signUpWithGoogle(
        token: String,
        nonce: String,
    ) {
        signInWithGoogleUseCase(
            token = token,
            nonce = nonce,
        ).onEach { result ->
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
                            isUserLoggedIn = true,
                            openProgressDialog = false,
                        )
                    }
                }
        }.launchIn(viewModelScope)
    }
}
