package edumate.app.presentation.enrolled

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.courses.GetEnrolledCoursesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class EnrolledViewModel @Inject constructor(
    private val getEnrolledCoursesUseCase: GetEnrolledCoursesUseCase
) : ViewModel() {

    var uiState by mutableStateOf(EnrolledUiState())
        private set

    init {
        fetchClasses()
    }

    private fun fetchClasses() {
        getEnrolledCoursesUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        loading = true,
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
                    Log.d("hello", "error: ${resource.message}")
                    uiState = uiState.copy(
                        loading = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}