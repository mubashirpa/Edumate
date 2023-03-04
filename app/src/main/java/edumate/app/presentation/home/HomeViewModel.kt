package edumate.app.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.SignOutUseCase
import edumate.app.domain.usecase.courses.GetEnrolledCoursesUseCase
import edumate.app.domain.usecase.courses.GetTeachingCoursesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val getEnrolledCoursesUseCase: GetEnrolledCoursesUseCase,
    private val getTeachingCoursesUseCase: GetTeachingCoursesUseCase
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        fetchRooms()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnOpenFabMenuChange -> {
                uiState = uiState.copy(openFabMenu = event.open)
            }
            is HomeUiEvent.SignOut -> {
                signOutUseCase()
            }
        }
    }

    private fun fetchRooms() {
        getTeachingCoursesUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        loading = true,
                        success = false,
                        error = false,
                        errorMessage = UiText.Empty
                    )
                }
                is Resource.Success -> {
                    uiState = uiState.copy(
                        loading = false,
                        success = true,
                        rooms = resource.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    Log.d("hello", "error: ${resource.message}")
                    uiState = uiState.copy(
                        loading = false,
                        error = true,
                        errorMessage = resource.message!!
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}