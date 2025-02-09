package app.edumate.presentation.viewStudentSubmission

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ViewStudentSubmissionViewModel(
    savedStateHandle: SavedStateHandle,
    private val getStudentSubmissionUseCase: GetStudentSubmissionUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(ViewStudentSubmissionUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.ViewStudentSubmission>()
    private var getStudentSubmissionJob: Job? = null

    init {
        getStudentSubmission(
            isRefreshing = false,
            courseId = args.courseId,
            courseWorkId = args.courseWorkId,
            studentId = args.studentId,
        )
    }

    fun onEvent(event: ViewStudentSubmissionUiEvent) {
        when (event) {
            is ViewStudentSubmissionUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is ViewStudentSubmissionUiEvent.OnOpenReturnDialogChange -> {
                uiState = uiState.copy(openReturnDialog = event.open)
            }

            ViewStudentSubmissionUiEvent.Refresh -> {
                getStudentSubmission(
                    isRefreshing = true,
                    courseId = args.courseId,
                    courseWorkId = args.courseWorkId,
                    studentId = args.studentId,
                )
            }

            ViewStudentSubmissionUiEvent.Retry -> {
                getStudentSubmission(
                    isRefreshing = false,
                    courseId = args.courseId,
                    courseWorkId = args.courseWorkId,
                    studentId = args.studentId,
                )
            }

            ViewStudentSubmissionUiEvent.Return -> TODO()

            ViewStudentSubmissionUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getStudentSubmission(
        isRefreshing: Boolean,
        courseId: String,
        courseWorkId: String,
        studentId: String,
    ) {
        // Cancel any ongoing getStudentSubmissionJob before making a new call.
        getStudentSubmissionJob?.cancel()
        getStudentSubmissionJob =
            getStudentSubmissionUseCase(courseId, courseWorkId, studentId)
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
                                    uiState.copy(studentSubmissionResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(studentSubmissionResult = result)
                                }
                        }

                        is Result.Success -> {
                            val submission = result.data!!
                            submission.assignedGrade?.let { grade ->
                                uiState.grade.setTextAndPlaceCursorAtEnd(grade.toString())
                            }

                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    studentSubmissionResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }
}
