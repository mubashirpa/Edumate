package edumate.app.presentation.student_work

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.student_submission.ListSubmissionsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class StudentWorkViewModel @Inject constructor(
    private val listSubmissionsUseCase: ListSubmissionsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(StudentWorkUiState())
        private set

    init {
        fetchStudentSubmissions()
    }

    private fun fetchStudentSubmissions() {
        listSubmissionsUseCase("2Mx7YrbyGUr8tsuBVr4x", "y0jyGrE1SqtAPRTTdg14").onEach { resource ->
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