package edumate.app.presentation.people

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.students.GetStudentsUseCase
import edumate.app.domain.usecase.teachers.GetTeachersUseCase
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStudentsUseCase: GetStudentsUseCase,
    private val getTeachersUseCase: GetTeachersUseCase
) : ViewModel() {

    var uiState by mutableStateOf(PeopleUiState())
        private set

    private val courseId: String? = savedStateHandle[Routes.Args.CLASS_DETAILS_COURSE_ID]

    init {
        fetchStudents(courseId.orEmpty())
        fetchTeachers(courseId.orEmpty())
    }

    fun onEvent(event: PeopleUiEvent) {
        when (event) {
            is PeopleUiEvent.OnFilterChange -> {
                uiState = uiState.copy(
                    showAll = event.peopleFilter == PeopleFilter.ALL,
                    showTeachers = event.peopleFilter == PeopleFilter.TEACHERS,
                    showStudents = event.peopleFilter == PeopleFilter.STUDENTS
                )
            }
        }
    }

    private fun fetchStudents(courseId: String) {
        getStudentsUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("hello", "loading students")
                }
                is Resource.Success -> {
                    Log.d("hello", "${resource.data}")
                    uiState = uiState.copy(students = resource.data ?: emptyList())
                }
                is Resource.Error -> {
                    Log.d("hello", "${resource.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchTeachers(courseId: String) {
        getTeachersUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("hello", "loading teachers")
                }
                is Resource.Success -> {
                    Log.d("hello", "${resource.data}")
                    uiState = uiState.copy(teachers = resource.data ?: emptyList())
                }
                is Resource.Error -> {
                    Log.d("hello", "${resource.message}")
                }
            }
        }.launchIn(viewModelScope)
    }
}