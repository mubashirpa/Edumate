package edumate.app.presentation.teaching

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.courses.GetTeachingCoursesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class TeachingViewModel @Inject constructor(
    private val getTeachingCoursesUseCase: GetTeachingCoursesUseCase
) : ViewModel() {

    var uiState by mutableStateOf(TeachingUiState())
        private set

    init {
        fetchClasses()
    }

    fun onEvent(event: TeachingUiEvent) {
        when (event) {
            is TeachingUiEvent.FetchClasses -> {
                fetchClasses()
            }
        }
    }

    private fun fetchClasses() {
        getTeachingCoursesUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        loading = true,
                        error = null,
                        success = false
                    )
                }
                is Resource.Success -> {
                    uiState = uiState.copy(
                        loading = false,
                        success = true,
                        classes = resource.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    uiState = uiState.copy(
                        loading = false,
                        error = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}