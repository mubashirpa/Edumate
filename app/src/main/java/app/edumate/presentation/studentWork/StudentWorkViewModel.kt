package app.edumate.presentation.studentWork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionsUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StudentWorkViewModel(
    savedStateHandle: SavedStateHandle,
    private val getStudentSubmissionsUseCase: GetStudentSubmissionsUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(StudentWorkUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.ViewCourseWork>()
    private var getStudentSubmissionsJob: Job? = null

    init {
        getStudentSubmissions(
            courseId = args.courseId,
            courseWorkId = args.id,
            isRefreshing = false,
        )
    }

    fun onEvent(event: StudentWorkUiEvent) {
        when (event) {
            StudentWorkUiEvent.Refresh -> {
                getStudentSubmissions(
                    courseId = args.courseId,
                    courseWorkId = args.id,
                    isRefreshing = true,
                )
            }

            StudentWorkUiEvent.Retry -> {
                getStudentSubmissions(
                    courseId = args.courseId,
                    courseWorkId = args.id,
                    isRefreshing = false,
                )
            }

            StudentWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getStudentSubmissions(
        courseId: String,
        courseWorkId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getStudentSubmissionsJob before making a new call.
        getStudentSubmissionsJob?.cancel()
        getStudentSubmissionsJob =
            getStudentSubmissionsUseCase(courseId, courseWorkId)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(
                                        isRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(studentSubmissionsResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(studentSubmissionsResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    studentSubmissionsResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }
}
