package app.edumate.presentation.courseDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.domain.usecase.courses.GetCourseUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CourseDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCourseUseCase: GetCourseUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(CourseDetailsUiState())
        private set

    val courseId = savedStateHandle.toRoute<Screen.CourseDetails>().courseId

    init {
        getCourse(courseId)
    }

    fun onEvent(event: CourseDetailsUiEvent) {
        when (event) {
            CourseDetailsUiEvent.OnRetry -> {
                getCourse(courseId)
            }
        }
    }

    private fun getCourse(id: String) {
        getCourseUseCase(id)
            .onEach { result ->
                uiState = uiState.copy(courseResult = result)
            }.launchIn(viewModelScope)
    }
}
