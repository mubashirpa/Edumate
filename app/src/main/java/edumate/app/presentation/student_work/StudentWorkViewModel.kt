package edumate.app.presentation.student_work

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.student_submission.GetStudentsUseCase
import edumate.app.domain.usecase.student_submission.ListSubmissionsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class StudentWorkViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val listSubmissionsUseCase: ListSubmissionsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(StudentWorkUiState())
        private set

    fun onEvent(event: StudentWorkUiEvent) {
        when (event) {
            is StudentWorkUiEvent.OnInit -> {
                fetchAssignedStudents(event.courseId)
                fetchStudentSubmissions(event.courseId, event.courseWorkId)
            }
        }
    }

    private fun fetchAssignedStudents(courseId: String) {
        getStudentsUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("hello", "Loading")
                }

                is Resource.Success -> {
                    Log.d("hello", "${resource.data}")
                    uiState = uiState.copy(assignedStudents = resource.data!!)
                }

                is Resource.Error -> {
                    Log.d("hello", "${resource.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchStudentSubmissions(courseId: String, courseWorkId: String) {
        listSubmissionsUseCase(courseId, courseWorkId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("hello", "Loading")
                }

                is Resource.Success -> {
                    Log.d("hello", "${resource.data}")
                    uiState = uiState.copy(studentSubmissions = resource.data!!)
                }

                is Resource.Error -> {
                    Log.d("hello", "${resource.message}")
                }
            }
        }.launchIn(viewModelScope)
    }
}