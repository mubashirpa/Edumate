package app.edumate.presentation.viewCourseWork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.usecase.courseWork.GetCourseWorkUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ViewCourseWorkViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCourseWorkUseCase: GetCourseWorkUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(ViewCourseWorkUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.ViewCourseWork>()
    private var getCourseWorkJob: Job? = null

    init {
        getCourseWork(
            id = args.id,
            isRefreshing = false,
            isCurrentUserStudent = args.isCurrentUserStudent,
        )
    }

    fun onEvent(event: ViewCourseWorkUiEvent) {
        when (event) {
            is ViewCourseWorkUiEvent.OnEditShortAnswerChange -> {
                uiState = uiState.copy(editShortAnswer = event.edit)
            }

            is ViewCourseWorkUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is ViewCourseWorkUiEvent.OnFilePicked -> TODO()

            is ViewCourseWorkUiEvent.OnMultipleChoiceAnswerValueChange -> {
                uiState = uiState.copy(multipleChoiceAnswer = event.answer)
            }

            is ViewCourseWorkUiEvent.OnOpenRemoveAttachmentDialogChange -> {
                uiState = uiState.copy(removeAttachmentIndex = event.index)
            }

            is ViewCourseWorkUiEvent.OnOpenTurnInDialogChange -> {
                uiState = uiState.copy(openTurnInDialog = event.open)
            }

            is ViewCourseWorkUiEvent.OnOpenUnSubmitDialogChange -> {
                uiState = uiState.copy(openUnSubmitDialog = event.open)
            }

            is ViewCourseWorkUiEvent.OnShowYourWorkBottomSheetChange -> {
                uiState = uiState.copy(showYourWorkBottomSheet = event.show)
            }

            ViewCourseWorkUiEvent.Reclaim -> TODO()

            ViewCourseWorkUiEvent.Refresh -> {
                getCourseWork(
                    id = args.id,
                    isRefreshing = true,
                    isCurrentUserStudent = args.isCurrentUserStudent,
                )
            }

            is ViewCourseWorkUiEvent.RemoveAttachment -> TODO()

            ViewCourseWorkUiEvent.Retry -> {
                if (uiState.courseWorkResult is Result.Error) {
                    getCourseWork(
                        id = args.id,
                        isRefreshing = false,
                        isCurrentUserStudent = args.isCurrentUserStudent,
                    )
                }
            }

            is ViewCourseWorkUiEvent.TurnIn -> TODO()

            ViewCourseWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getCourseWork(
        id: String,
        isRefreshing: Boolean,
        isCurrentUserStudent: Boolean,
    ) {
        // Cancel any ongoing getCourseWorkJob before making a new call.
        getCourseWorkJob?.cancel()
        getCourseWorkJob =
            getCourseWorkUseCase(id)
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
                                    uiState.copy(courseWorkResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(courseWorkResult = result)
                                }
                        }

                        is Result.Success -> {
                            // When Result.Success, if the user is a student,
                            // update the UI state with the course work result and then load the student submission.
                            // Otherwise, update the UI state with the course work result and stop refreshing.
                            if (isCurrentUserStudent) {
                                uiState = uiState.copy(courseWorkResult = result)
                                // When Result.Success, load the student submission.
                                // getStudentSubmission(isRefreshing, uiState.userId.orEmpty()) TODO
                            } else {
                                uiState =
                                    uiState.copy(
                                        courseWorkResult = result,
                                        isRefreshing = false,
                                    )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
    }
}
