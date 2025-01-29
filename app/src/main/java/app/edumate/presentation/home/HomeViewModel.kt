package app.edumate.presentation.home

import android.net.Uri
import android.webkit.URLUtil
import androidx.compose.foundation.text.input.TextFieldState
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
import app.edumate.domain.usecase.course.DeleteCourseUseCase
import app.edumate.domain.usecase.course.GetCoursesUseCase
import app.edumate.domain.usecase.member.JoinCourseUseCase
import app.edumate.domain.usecase.member.UnenrollCourseUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCoursesUseCase: GetCoursesUseCase,
    private val joinCourseUseCase: JoinCourseUseCase,
    private val unenrollCourseUseCase: UnenrollCourseUseCase,
    private val deleteCourseUseCase: DeleteCourseUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.Home>()
    private var getCoursesJob: Job? = null

    init {
        getCurrentUser()
        getCourses(refreshing = false)

        if (!args.courseId.isNullOrEmpty() && args.enrollmentCode != null) {
            uiState =
                uiState.copy(
                    joinCourseUiState =
                        uiState.joinCourseUiState.copy(
                            courseId =
                                TextFieldState(
                                    args.courseId,
                                ),
                            showBottomSheet = true,
                        ),
                )
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.DeleteCourse -> deleteCourse(event.courseId)

            is HomeUiEvent.JoinCourse -> joinCourse(event.courseId.trim())

            is HomeUiEvent.LeaveCourse -> unenrollCourse(event.courseId)

            is HomeUiEvent.OnAppBarDropdownExpandedChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is HomeUiEvent.OnOpenDeleteCourseDialogChange -> {
                uiState = uiState.copy(deleteCourseId = event.courseId)
            }

            is HomeUiEvent.OnOpenLeaveCourseDialogChange -> {
                uiState = uiState.copy(leaveCourse = event.course)
            }

            is HomeUiEvent.OnOpenUnenrollDialogChange -> {
                uiState = uiState.copy(unenrollCourseId = event.courseId)
            }

            HomeUiEvent.OnRefresh -> {
                getCourses(refreshing = uiState.coursesResult is Result.Success)
            }

            HomeUiEvent.OnRetry -> getCourses(refreshing = false)

            is HomeUiEvent.OnShowAddCourseBottomSheetChange -> {
                uiState = uiState.copy(showAddCourseBottomSheet = event.show)
            }

            is HomeUiEvent.OnShowJoinCourseBottomSheetChange -> {
                uiState =
                    uiState.copy(joinCourseUiState = JoinCourseBottomSheetUiState(showBottomSheet = event.show))
            }

            is HomeUiEvent.UnenrollCourse -> unenrollCourse(event.courseId)

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
                            val courses = result.data.orEmpty()

                            uiState =
                                uiState.copy(
                                    coursesResult = result,
                                    enrolledCourses = courses.filter { it.role == UserRole.STUDENT },
                                    isRefreshing = false,
                                    teachingCourses = courses.filter { it.role == UserRole.TEACHER },
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

        val joinId =
            if (URLUtil.isValidUrl(courseId)) {
                val uri = Uri.parse(courseId)
                uri.lastPathSegment ?: ""
            } else {
                courseId
            }

        joinCourseUseCase(joinId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                joinCourseUiState = uiState.joinCourseUiState.copy(error = result.message),
                                openProgressDialog = false,
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                joinCourseUiState =
                                    uiState.joinCourseUiState.copy(
                                        courseIdError = null,
                                        error = null,
                                    ),
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState =
                            uiState.copy(
                                joinCourseUiState = uiState.joinCourseUiState.copy(showBottomSheet = false),
                                openProgressDialog = false,
                            )
                        getCourses(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun unenrollCourse(courseId: String) {
        unenrollCourseUseCase(courseId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                leaveCourse = null,
                                openProgressDialog = true,
                                unenrollCourseId = null,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        getCourses(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deleteCourse(courseId: String) {
        deleteCourseUseCase(courseId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                deleteCourseId = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        getCourses(true)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
