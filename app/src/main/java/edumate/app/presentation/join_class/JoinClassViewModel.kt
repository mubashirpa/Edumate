package edumate.app.presentation.join_class

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.students.AddStudentUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class JoinClassViewModel @Inject constructor(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val addStudentUseCase: AddStudentUseCase,
    private val validateTextField: ValidateTextField
) : ViewModel() {

    var uiState by mutableStateOf(JoinClassUiState())
        private set

    private val resultChannel = Channel<String>()
    val joinClassResults = resultChannel.receiveAsFlow()

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: JoinClassUiEvent) {
        when (event) {
            is JoinClassUiEvent.ClassCodeChanged -> {
                uiState = uiState.copy(
                    classCode = event.classCode,
                    classCodeError = null
                )
            }
            is JoinClassUiEvent.OnJoinClick -> {
                joinClass()
            }
            is JoinClassUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun joinClass() {
        val classCodeResult = validateTextField.execute(uiState.classCode)
        uiState = uiState.copy(classCodeError = UiText.StringResource(R.string.enter_class_code))

        if (!classCodeResult.successful) return

        addStudentUseCase(uiState.classCode).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    resultChannel.send(uiState.classCode)
                }
                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}