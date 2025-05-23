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
import app.edumate.domain.usecase.preferences.GetUserPreferencesUseCase
import app.edumate.domain.usecase.preferences.UpdateReviewDialogShownUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CourseDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCourseWithMembersUseCase: GetCourseWithMembersUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updateReviewDialogShownUseCase: UpdateReviewDialogShownUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(CourseDetailsUiState())
        private set

    private val courseId = savedStateHandle.toRoute<Screen.CourseDetails>().courseId
    private var currentUserId: String? = null

    init {
        getCurrentUser()
        getUserPreferences()
        getCourse()
    }

    fun onEvent(event: CourseDetailsUiEvent) {
        when (event) {
            CourseDetailsUiEvent.Retry -> {
                getCourse()
            }

            CourseDetailsUiEvent.ReviewDialogShown -> {
                updateReviewDialogShownTime()
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    result.data?.id?.let { userId ->
                        currentUserId = userId
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getCourse() {
        getCourseWithMembersUseCase(courseId)
            .onEach { result ->
                uiState =
                    when (result) {
                        is Result.Success -> {
                            val course = result.data!!
                            val currentMember = course.members?.find { it.userId == currentUserId }

                            // Determine the role of the current user within the course
                            val currentUserRole =
                                when (currentMember?.role) {
                                    UserRole.TEACHER -> {
                                        CourseUserRole.Teacher(
                                            isCourseOwner = course.ownerId == currentUserId,
                                        )
                                    }

                                    UserRole.STUDENT -> {
                                        CourseUserRole.Student
                                    }

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

                        else -> {
                            uiState.copy(courseResult = result)
                        }
                    }
            }.launchIn(viewModelScope)
    }

    private fun getUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesUseCase().collect { userPreferences ->
                val reviewTimeout = TimeUnit.MILLISECONDS.convert(4, TimeUnit.DAYS)
                val reviewShownAt = userPreferences.lastReviewShownAt

                when {
                    reviewShownAt == 0L -> {
                        updateReviewDialogShownTime()
                    }

                    else -> {
                        val open = System.currentTimeMillis() - reviewShownAt >= reviewTimeout
                        uiState = uiState.copy(openReviewDialog = open)
                    }
                }
            }
        }
    }

    private fun updateReviewDialogShownTime() {
        updateReviewDialogShownUseCase(System.currentTimeMillis()).launchIn(viewModelScope)
    }
}
