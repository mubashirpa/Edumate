package app.edumate.presentation.courseDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.course.GetCourseWithMembersUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CourseDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCourseWithMembersUseCase: GetCourseWithMembersUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(CourseDetailsUiState())
        private set

    val args = savedStateHandle.toRoute<Screen.CourseDetails>()
    var currentUserId: String? = null

    init {
        getCurrentUser()
        getCourse(args.courseId)
    }

    fun onEvent(event: CourseDetailsUiEvent) {
        when (event) {
            CourseDetailsUiEvent.Retry -> {
                getCourse(args.courseId)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    currentUserId = result.data?.id
                }
            }.launchIn(viewModelScope)
    }

    private fun getCourse(courseId: String) {
        getCourseWithMembersUseCase(courseId)
            .onEach { result ->
                uiState =
                    when (result) {
                        is Result.Success -> {
                            val course = result.data
                            val currentMember = course?.members?.find { it.userId == currentUserId }

                            // Determine the role of the current user within the course
                            val currentUserRole =
                                when (currentMember?.role) {
                                    UserRole.TEACHER -> {
                                        CourseUserRole.Teacher(
                                            isCourseOwner = course.ownerId == currentUserId,
                                        )
                                    }

                                    UserRole.STUDENT -> CourseUserRole.Student

                                    // Handle the case where the user's role is null (unexpected scenario)
                                    null -> {
                                        // Update UI state to reflect an error due to an unexpected null role
                                        uiState =
                                            uiState.copy(
                                                courseResult =
                                                    Result.Error(
                                                        UiText.StringResource(R.string.class_not_found),
                                                    ),
                                            )
                                        return@onEach // Exit the flow collection early
                                    }
                                }

                            uiState.copy(
                                currentUserRole = currentUserRole,
                                courseResult = result,
                            )
                        }

                        else -> uiState.copy(courseResult = result)
                    }
            }.launchIn(viewModelScope)
    }
}
