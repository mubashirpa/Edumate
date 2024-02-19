package edumate.app.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.CreateUserUseCase
import edumate.app.domain.usecase.authentication.GoogleSignInUseCase
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.domain.usecase.validation.ValidateName
import edumate.app.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val createUserUseCase: CreateUserUseCase,
        private val googleSignInUseCase: GoogleSignInUseCase,
        private val validateEmail: ValidateEmail,
        private val validateName: ValidateName,
        private val validatePassword: ValidatePassword,
    ) : ViewModel() {
        var uiState by mutableStateOf(RegisterUiState())
            private set

        fun onEvent(event: RegisterUiEvent) {
            when (event) {
                is RegisterUiEvent.OnEmailValueChange -> {
                    uiState =
                        uiState.copy(
                            email = event.email,
                            emailError = null,
                        )
                }

                is RegisterUiEvent.OnNameValueChange -> {
                    uiState =
                        uiState.copy(
                            name = event.name,
                            nameError = null,
                        )
                }

                is RegisterUiEvent.OnPasswordValueChange -> {
                    uiState =
                        uiState.copy(
                            password = event.password,
                            passwordError = null,
                        )
                }

                is RegisterUiEvent.SignInWithGoogle -> {
                    signUpWithGoogle(event.token)
                }

                is RegisterUiEvent.SignUp -> {
                    createUserWithEmailAndPassword(
                        name = uiState.name.trim(),
                        email = uiState.email.trim(),
                        password = uiState.password.trim(),
                    )
                }

                is RegisterUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun createUserWithEmailAndPassword(
            name: String,
            email: String,
            password: String,
        ) {
            val nameResult = validateName.execute(name)
            val emailResult = validateEmail.execute(email)
            val passwordResult = validatePassword.execute(password)

            uiState =
                uiState.copy(
                    nameError = nameResult.error,
                    emailError = emailResult.error,
                    passwordError = passwordResult.error,
                )

            val hasError =
                listOf(
                    nameResult,
                    emailResult,
                    passwordResult,
                ).any { !it.successful }

            if (hasError) return

            createUserUseCase(name, email, password).onEach { result ->
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

        private fun signUpWithGoogle(idToken: String) {
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
            }
        }
    }
