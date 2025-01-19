package app.edumate.presentation.signIn

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.usecase.authentication.GetSignInInfoUseCase
import app.edumate.domain.usecase.authentication.SignInUseCase
import app.edumate.domain.usecase.authentication.SignInWithGoogleUseCase
import app.edumate.domain.usecase.validation.ValidateEmail
import app.edumate.domain.usecase.validation.ValidatePassword
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getSignInInfoUseCase: GetSignInInfoUseCase,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    var uiState by mutableStateOf(SignInUiState())
        private set

    init {
        getSignInInfo()
    }

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.OnRememberSwitchCheckedChange -> {
                uiState = uiState.copy(rememberPassword = event.checked)
            }

            SignInUiEvent.SignIn -> {
                signIn(
                    email = uiState.email.text.toString(),
                    password = uiState.password.text.toString(),
                )
            }

            is SignInUiEvent.SignInWithGoogle -> {
                signInWithGoogle(token = event.token, nonce = event.nonce)
            }

            SignInUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun signIn(
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

        signInUseCase(
            email = email,
            password = password,
            remember = uiState.rememberPassword,
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

    private fun signInWithGoogle(
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

    private fun getSignInInfo() {
        viewModelScope.launch {
            getSignInInfoUseCase().firstOrNull()?.let { preferences ->
                uiState.email.setTextAndPlaceCursorAtEnd(preferences.email)
                uiState.password.setTextAndPlaceCursorAtEnd(preferences.password)
                uiState = uiState.copy(rememberPassword = preferences.email.isNotEmpty())
            }
        }
    }
}
