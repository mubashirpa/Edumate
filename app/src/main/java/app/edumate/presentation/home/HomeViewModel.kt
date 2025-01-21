package app.edumate.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.courses.GetCoursesUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCoursesUseCase: GetCoursesUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    private var getCoursesJob: Job? = null

    init {
        getCurrentUser()
        getCourses(refreshing = false)
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.JoinCourse -> {
                joinCourse(event.courseId.trim())
            }

            is HomeUiEvent.OnAppBarDropdownExpandedChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            HomeUiEvent.OnRefresh -> {
                if (uiState.coursesResult is Result.Success) {
                    getCourses(refreshing = true)
                } else {
                    getCourses(refreshing = false)
                }
            }

            HomeUiEvent.OnRetry -> {
                getCourses(refreshing = false)
            }

            is HomeUiEvent.OnShowAddCourseBottomSheetChange -> {
                uiState = uiState.copy(showAddCourseBottomSheet = event.show)
            }

            is HomeUiEvent.OnShowJoinCourseBottomSheetChange -> {
                uiState =
                    uiState.copy(joinCourseUiState = JoinCourseBottomSheetUiState(showBottomSheet = event.show))
            }

            HomeUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    uiState = uiState.copy(currentUser = result.data)
                }
            }.launchIn(viewModelScope)
    }

    private fun getCourses(refreshing: Boolean) {
        // Cancel any ongoing getCoursesJob before making a new call.
        getCoursesJob?.cancel()
        getCoursesJob =
            getCoursesUseCase()
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (refreshing) {
                                    uiState.copy(
                                        isRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(coursesResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with refreshing = true.
                            uiState =
                                if (refreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(coursesResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    coursesResult = result,
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun joinCourse(courseId: String) {
        val courseIdResult = validateTextField.execute(courseId)
        if (!courseIdResult.successful) {
            uiState =
                uiState.copy(
                    joinCourseUiState =
                        uiState.joinCourseUiState.copy(
                            courseIdError = UiText.StringResource(R.string.ask_teacher_for_class_code),
                        ),
                )
            return
        }
        uiState =
            uiState.copy(
                joinCourseUiState =
                    uiState.joinCourseUiState.copy(
                        courseIdError = null,
                        showBottomSheet = false,
                    ),
            )

        // TODO: Join course
    }
}
