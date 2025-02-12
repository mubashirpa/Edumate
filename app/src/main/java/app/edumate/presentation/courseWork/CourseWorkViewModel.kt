package app.edumate.presentation.courseWork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.usecase.courseWork.DeleteCourseWorkUseCase
import app.edumate.domain.usecase.courseWork.GetCourseWorksUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CourseWorkViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCourseWorksUseCase: GetCourseWorksUseCase,
    private val deleteCourseWorkUseCase: DeleteCourseWorkUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(CourseWorkUiState())
        private set

    private val courseId = savedStateHandle.toRoute<Screen.CourseWork>().courseId
    private var getCourseWorksJob: Job? = null

    init {
        getCourseWorks(false)
    }

    fun onEvent(event: CourseWorkUiEvent) {
        when (event) {
            is CourseWorkUiEvent.DeleteCourseWork -> {
                deleteCourseWork(event.courseWorkId)
            }

            is CourseWorkUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is CourseWorkUiEvent.OnOpenDeleteCourseWorkDialogChange -> {
                uiState = uiState.copy(deleteCourseWork = event.courseWork)
            }

            is CourseWorkUiEvent.OnShowCreateCourseWorkBottomSheetChange -> {
                uiState = uiState.copy(showCreateCourseWorkBottomSheet = event.show)
            }

            CourseWorkUiEvent.Refresh -> {
                getCourseWorks(uiState.courseWorkResult is Result.Success)
            }

            CourseWorkUiEvent.Retry -> {
                getCourseWorks(false)
            }

            CourseWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getCourseWorks(isRefreshing: Boolean) {
        // Cancel any ongoing getCourseWorksJob before making a new call.
        getCourseWorksJob?.cancel()
        getCourseWorksJob =
            getCourseWorksUseCase(courseId)
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
                            uiState =
                                uiState.copy(
                                    courseWorkResult = result,
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun deleteCourseWork(courseWorkId: String) {
        deleteCourseWorkUseCase(courseWorkId)
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
                                deleteCourseWork = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        getCourseWorks(true)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
