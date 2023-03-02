package edumate.app.presentation.create_room

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.rooms.CreatedBy
import edumate.app.domain.model.rooms.Room
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.rooms.CreateRoomUseCase
import edumate.app.domain.usecase.rooms.DeleteRoomUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateRoomViewModel @Inject constructor(
    private val createRoomUseCase: CreateRoomUseCase,
    private val deleteRoomUseCase: DeleteRoomUseCase,
    private val validateTextField: ValidateTextField,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CreateRoomUiState())
        private set

    private val resultChannel = Channel<String>()
    val createRoomResults = resultChannel.receiveAsFlow()

    val room = mutableStateOf(Room())

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
            if (user != null) {
                room.value = room.value.copy(
                    createdBy = CreatedBy(user.displayName, user.uid),
                    teachers = arrayListOf(user.uid)
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: CreateRoomUiEvent) {
        when (event) {
            is CreateRoomUiEvent.NameChanged -> {
                uiState = uiState.copy(
                    name = event.name,
                    nameError = null
                )
                room.value = room.value.copy(title = event.name)
            }
            is CreateRoomUiEvent.SectionChanged -> {
                uiState = uiState.copy(section = event.section)
                room.value = room.value.copy(section = event.section)
            }
            is CreateRoomUiEvent.SubjectChanged -> {
                uiState = uiState.copy(subject = event.subject)
                room.value = room.value.copy(subject = event.subject)
            }
            is CreateRoomUiEvent.OnCreateClick -> {
                createRoom()
            }
            is CreateRoomUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createRoom() {
        val nameResult = validateTextField.execute(uiState.name)
        uiState = uiState.copy(nameError = nameResult.error)

        if (!nameResult.successful) return

        val uid = uiState.currentUser?.uid
        if (uid != null) {
            createRoomUseCase(room.value, uid).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }
                    is Resource.Success -> {
                        val roomId = resource.data
                        if (roomId != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(resource.data)
                        } else {
                            uiState = uiState.copy(
                                openProgressDialog = false,
                                userMessage = UiText.StringResource(Strings.error_unknown)
                            )
                        }
                    }
                    is Resource.Error -> {
                        if (resource.data != null) {
                            deleteRoomUseCase(resource.data).launchIn(viewModelScope)
                        }
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = resource.message
                        )
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            uiState = uiState.copy(userMessage = UiText.StringResource(Strings.error_unexpected))
        }
    }
}