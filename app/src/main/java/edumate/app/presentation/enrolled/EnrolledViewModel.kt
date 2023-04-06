package edumate.app.presentation.enrolled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.GetEnrolledCoursesUseCase
import edumate.app.domain.usecase.students.DeleteStudentUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class EnrolledViewModel @Inject constructor(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getEnrolledCoursesUseCase: GetEnrolledCoursesUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase
) : ViewModel() {

    var uiState by mutableStateOf(EnrolledUiState())
        private set

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
        fetchClasses()
    }

    fun onEvent(event: EnrolledUiEvent) {
        when (event) {
            is EnrolledUiEvent.Unenroll -> {
                uiState.currentUser?.uid?.let { uid ->
                    unEnroll(event.courseId, uid)
                }
            }
            is EnrolledUiEvent.FetchClasses -> {
                fetchClasses()
            }
            is EnrolledUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchClasses() {
        getEnrolledCoursesUseCase().onEach { resource ->
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

    private fun unEnroll(courseId: String, uid: String) {
        deleteStudentUseCase(courseId, uid).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchClasses()
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