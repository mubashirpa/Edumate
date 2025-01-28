package app.edumate.presentation.courseDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.usecase.courses.GetCourseWithCurrentUserUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CourseDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCourseWithCurrentUserUseCase: GetCourseWithCurrentUserUseCase,
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
        getCourseWithCurrentUserUseCase(id)
            .onEach { result ->
                uiState =
                    when (result) {
                        is Result.Success -> {
                            val course = result.data
                            val currentUser = course?.members?.firstOrNull()
                            val currentUserRole =
                                when (currentUser?.role) {
                                    UserRole.TEACHER -> {
                                        if (course.owner?.id == currentUser.userId) {
                                            CurrentUserRole.OWNER
                                        } else {
                                            CurrentUserRole.TEACHER
                                        }
                                    }

                                    else -> CurrentUserRole.STUDENT
                                }
                            uiState.copy(currentUserRole = currentUserRole, courseResult = result)
                        }

                        else -> uiState.copy(courseResult = result)
                    }
            }.launchIn(viewModelScope)
    }
}
