package edumate.app.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.authentication.GoogleSignInUseCase
import edumate.app.domain.usecase.authentication.SignInUseCase
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.domain.usecase.validation.ValidatePassword
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                uiState = uiState.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is LoginUiEvent.OnGoogleSignInClick -> {
                signInWithGoogle(event.token)
            }
            is LoginUiEvent.PasswordChanged -> {
                uiState = uiState.copy(
                    password = event.password,
                    passwordError = null
                )
            }
            is LoginUiEvent.OnSignInClick -> {
                signInWithEmailAndPassword(uiState.email, uiState.password)
            }
            is LoginUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        val emailResult = validateEmail.execute(email)
        val passwordResult = validatePassword.execute(password)

        uiState = uiState.copy(
            emailError = emailResult.error,
            passwordError = passwordResult.error
        )

        val hasError = listOf(
            emailResult,
            passwordResult
        ).any { !it.successful }

        if (hasError) return

        signInUseCase(email, password).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    uiState.copy(
                        openProgressDialog = false,
                        isUserLoggedIn = true
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

    private fun signInWithGoogle(idToken: String) {
        googleSignInUseCase(idToken).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    uiState.copy(
                        openProgressDialog = false,
                        isUserLoggedIn = true
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