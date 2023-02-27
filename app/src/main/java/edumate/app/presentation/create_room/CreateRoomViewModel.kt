package edumate.app.presentation.create_room

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.model.Room
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.rooms.AddRoomsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class CreateRoomViewModel @Inject constructor(
    private val addRoomsUseCase: AddRoomsUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CreateRoomUiState())
        private set

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: CreateRoomUiEvent) {
        when (event) {
            is CreateRoomUiEvent.NameChanged -> {
                uiState = uiState.copy(name = event.name)
            }
            is CreateRoomUiEvent.SectionChanged -> {
                uiState = uiState.copy(section = event.section)
            }
            is CreateRoomUiEvent.SubjectChanged -> {
                uiState = uiState.copy(subject = event.subject)
            }
            is CreateRoomUiEvent.OnCreateClick -> {
                createRoom()
            }
        }
    }

    private fun createRoom() {
        addRoomsUseCase(
            Room(
                members = arrayListOf(uiState.currentUser?.uid.orEmpty()),
                title = uiState.name,
                section = uiState.section,
                subject = uiState.subject
            )
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("hello", "loading")
                }
                is Resource.Success -> {
                    Log.d("hello", "success: ${resource.data}")
                }
                is Resource.Error -> {
                    Log.d("hello", "error: ${resource.message}")
                }
            }
        }.launchIn(viewModelScope)
    }
}