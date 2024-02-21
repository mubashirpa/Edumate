package edumate.app.presentation.teaching

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.courses.DeleteCourseUseCase
import edumate.app.domain.usecase.classroom.courses.ListCoursesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TeachingViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val listCoursesUseCase: ListCoursesUseCase,
        private val deleteCourseUseCase: DeleteCourseUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(TeachingUiState())
            private set

        private var listCoursesJob: Job? = null

        init {
            getCurrentUserUseCase().map { user ->
                if (user != null) {
                    uiState = uiState.copy(userId = user.uid)
                    getCourses(user.uid, false)
                }
            }.launchIn(viewModelScope)
        }

        fun onEvent(event: TeachingUiEvent) {
            when (event) {
                is TeachingUiEvent.DeleteCourse -> {
                    val userId = uiState.userId
                    if (userId != null) {
                        deleteCourse(event.courseId, userId)
                    }
                }

                is TeachingUiEvent.OnOpenDeleteCourseDialogChange -> {
                    uiState = uiState.copy(deleteCourseId = event.courseId)
                }

                TeachingUiEvent.Refresh -> {
                    val userId = uiState.userId
                    if (userId != null) {
                        getCourses(userId, true)
                    }
                }

                TeachingUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun getCourses(
            teacherId: String,
            refreshing: Boolean,
        ) {
            // Cancel any ongoing listCoursesJob before making a new call.
            listCoursesJob?.cancel()
            listCoursesJob =
                listCoursesUseCase(teacherId = teacherId).onEach { result ->
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
                                    uiState.copy(teachingCoursesResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with refreshing = true.
                            uiState =
                                if (refreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(teachingCoursesResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    teachingCoursesResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        private fun deleteCourse(
            courseId: String,
            userId: String,
        ) {
            deleteCourseUseCase(courseId).onEach { result ->
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
                        getCourses(userId, true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
