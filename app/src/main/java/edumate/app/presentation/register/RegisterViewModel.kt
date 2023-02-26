package edumate.app.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.authentication.CreateUserUseCase
import edumate.app.domain.usecase.authentication.GoogleSignInUseCase
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.domain.usecase.validation.ValidateName
import edumate.app.domain.usecase.validation.ValidatePassword
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.NameChanged -> {
                uiState = uiState.copy(
                    name = event.name,
                    nameError = null
                )
            }
            is RegisterUiEvent.EmailChanged -> {
                uiState = uiState.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is RegisterUiEvent.PasswordChanged -> {
                uiState = uiState.copy(
                    password = event.password,
                    passwordError = null
                )
            }
            is RegisterUiEvent.OnSignUpClick -> {
                createUserWithEmailAndPassword(
                    uiState.name,
                    uiState.email,
                    uiState.password
                )
            }
            is RegisterUiEvent.OnGoogleSignUpClick -> {
                signUpWithGoogle(event.token)
            }
            is RegisterUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createUserWithEmailAndPassword(name: String, email: String, password: String) {
        val nameResult = validateName.execute(name)
        val emailResult = validateEmail.execute(email)
        val passwordResult = validatePassword.execute(password)

        uiState = uiState.copy(
            nameError = nameResult.error,
            emailError = emailResult.error,
            passwordError = passwordResult.error
        )

        val hasError = listOf(
            nameResult,
            emailResult,
            passwordResult
        ).any { !it.successful }

        if (hasError) return

        createUserUseCase(name, email, password).onEach { resource ->
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

    private fun signUpWithGoogle(idToken: String) {
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