package edumate.app.presentation.student_work

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.student_submissions.ListSubmissions
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

    private lateinit var courseId: String
    private lateinit var courseWorkId: String

    fun onEvent(event: StudentWorkUiEvent) {
        when (event) {
            is StudentWorkUiEvent.OnInit -> {
                courseId = event.courseId
                courseWorkId = event.courseWorkId
                if (uiState.dataState == DataState.UNKNOWN) {
                    fetchAssignedStudents(false)
                }
            }

            StudentWorkUiEvent.OnRefresh -> {
                fetchAssignedStudents(true)
            }

            StudentWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchAssignedStudents(refreshing: Boolean) {
        getStudentsUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val assignedStudents = resource.data
                    if (assignedStudents.isNullOrEmpty()) {
                        uiState = uiState.copy(
                            dataState = DataState.EMPTY(
                                UiText.StringResource(Strings.get_started_by_inviting_students)
                            ),
                            refreshing = false
                        )
                    } else {
                        uiState = uiState.copy(assignedStudents = assignedStudents)
                        fetchStudentSubmissions(refreshing)
                    }
                }

                is Resource.Error -> {
                    uiState = if (refreshing) {
                        uiState.copy(
                            refreshing = false,
                            userMessage = resource.message
                        )
                    } else {
                        uiState.copy(dataState = DataState.ERROR(message = resource.message!!))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchStudentSubmissions(refreshing: Boolean) {
        listSubmissions(courseId, courseWorkId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val studentSubmissions = resource.data ?: emptyList()
                    uiState = uiState.copy(
                        dataState = DataState.SUCCESS,
                        refreshing = false,
                        studentSubmissions = studentSubmissions
                    )
                }

                is Resource.Error -> {
                    uiState = if (refreshing) {
                        uiState.copy(
                            refreshing = false,
                            userMessage = resource.message
                        )
                    } else {
                        uiState.copy(dataState = DataState.ERROR(message = resource.message!!))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}