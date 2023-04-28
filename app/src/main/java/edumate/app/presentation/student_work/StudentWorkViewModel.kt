package edumate.app.presentation.student_work

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.student_submission.ListSubmissions
import edumate.app.domain.usecase.students.GetStudentsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class StudentWorkViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val listSubmissions: ListSubmissions
) : ViewModel() {

    var uiState by mutableStateOf(StudentWorkUiState())
        private set

    fun onEvent(event: StudentWorkUiEvent) {
        when (event) {
            is StudentWorkUiEvent.OnInit -> {
                if (uiState.dataState == DataState.UNKNOWN) {
                    fetchAssignedStudents(event.courseId, event.courseWorkId)
                }
            }
        }
    }

    private fun fetchAssignedStudents(courseId: String, courseWorkId: String) {
        getStudentsUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(dataState = DataState.LOADING)
                }

                is Resource.Success -> {
                    val assignedStudents = resource.data
                    uiState = if (assignedStudents.isNullOrEmpty()) {
                        uiState.copy(
                            dataState = DataState.EMPTY(
                                UiText.DynamicString("Get started by inviting students")
                            )
                        )
                    } else {
                        fetchStudentSubmissions(courseId, courseWorkId)
                        uiState.copy(assignedStudents = assignedStudents)
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(dataState = DataState.ERROR(resource.message!!))
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchStudentSubmissions(courseId: String, courseWorkId: String) {
        listSubmissions(courseId, courseWorkId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    val studentSubmissions = resource.data
                    if (studentSubmissions != null) {
                        uiState = uiState.copy(
                            dataState = DataState.SUCCESS,
                            studentSubmissions = studentSubmissions
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(dataState = DataState.ERROR(resource.message!!))
                }
            }
        }.launchIn(viewModelScope)
    }
}