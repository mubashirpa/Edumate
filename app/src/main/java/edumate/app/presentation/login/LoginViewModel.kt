package edumate.app.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.GoogleSignInUseCase
import edumate.app.domain.usecase.authentication.SignInUseCase
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val googleSignInUseCase: GoogleSignInUseCase,
        private val signInUseCase: SignInUseCase,
        private val validateEmail: ValidateEmail,
        private val validatePassword: ValidatePassword,
    ) : ViewModel() {
        var uiState by mutableStateOf(LoginUiState())
            private set

        fun onEvent(event: LoginUiEvent) {
            when (event) {
                is LoginUiEvent.OnEmailValueChange -> {
                    uiState =
                        uiState.copy(
                            email = event.email,
                            emailError = null,
                        )
                }

                is LoginUiEvent.OnPasswordValueChange -> {
                    uiState =
                        uiState.copy(
                            password = event.password,
                            passwordError = null,
                        )
                }

                is LoginUiEvent.SignInWithGoogle -> {
                    signInWithGoogle(event.token)
                }

                is LoginUiEvent.SignIn -> {
                    signInWithEmailAndPassword(
                        email = uiState.email.trim(),
                        password = uiState.password.trim(),
                    )
                }

                is LoginUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun signInWithEmailAndPassword(
            email: String,
            password: String,
        ) {
            val emailResult = validateEmail.execute(email)
            val passwordResult = validatePassword.execute(password)

            uiState =
                uiState.copy(
                    emailError = emailResult.error,
                    passwordError = passwordResult.error,
                )

            val hasError =
                listOf(
                    emailResult,
                    passwordResult,
                ).any { !it.successful }

            if (hasError) return

            signInUseCase(email, password).onEach { result ->
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

        private fun signInWithGoogle(idToken: String) {
            googleSignInUseCase(idToken).onEach { result ->
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
