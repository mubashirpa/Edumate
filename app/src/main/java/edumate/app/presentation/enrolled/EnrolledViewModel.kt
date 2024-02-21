package edumate.app.presentation.enrolled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.courses.ListCoursesUseCase
import edumate.app.domain.usecase.classroom.students.DeleteStudentUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class EnrolledViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val listCoursesUseCase: ListCoursesUseCase,
        private val deleteStudentUseCase: DeleteStudentUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(EnrolledUiState())
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

        fun onEvent(event: EnrolledUiEvent) {
            when (event) {
                is EnrolledUiEvent.OnOpenUnEnrolDialogChange -> {
                    uiState = uiState.copy(unEnrollCourseId = event.courseId)
                }

                is EnrolledUiEvent.UnEnroll -> {
                    val userId = uiState.userId
                    if (userId != null) {
                        unEnroll(event.courseId, userId)
                    }
                }

                EnrolledUiEvent.Refresh -> {
                    val userId = uiState.userId
                    if (userId != null) {
                        getCourses(userId, true)
                    }
                }

                EnrolledUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun getCourses(
            studentId: String,
            refreshing: Boolean,
        ) {
            // Cancel any ongoing listCoursesJob before making a new call.
            listCoursesJob?.cancel()
            listCoursesJob =
                listCoursesUseCase(studentId = studentId).onEach { result ->
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
                                    uiState.copy(enrolledCoursesResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with refreshing = true.
                            uiState =
                                if (refreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(enrolledCoursesResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    enrolledCoursesResult = result,
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        private fun unEnroll(
            courseId: String,
            userId: String,
        ) {
            deleteStudentUseCase(courseId, userId).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = UiText.StringResource(Strings.unable_to_leave_class),
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = true,
                                unEnrollCourseId = null,
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
