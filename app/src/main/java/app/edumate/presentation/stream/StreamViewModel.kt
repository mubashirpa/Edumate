package app.edumate.presentation.stream

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.domain.usecase.announcement.GetAnnouncementsUseCase
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StreamViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(StreamUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.Stream>()
    private var getAnnouncementsJob: Job? = null

    init {
        getCurrentUser()
        getAnnouncements(
            courseId = args.courseId,
            isRefreshing = false,
        )
    }

    fun onEvent(event: StreamUiEvent) {
        when (event) {
            is StreamUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            StreamUiEvent.OnRefresh -> {
                getAnnouncements(
                    courseId = args.courseId,
                    isRefreshing = true,
                )
            }

            StreamUiEvent.OnRetry -> {
                getAnnouncements(
                    courseId = args.courseId,
                    isRefreshing = false,
                )
            }

            StreamUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    result.data?.id?.let { userId ->
                        uiState = uiState.copy(currentUserId = userId)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getAnnouncements(
        courseId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getAnnouncementsJob before making a new call.
        getAnnouncementsJob?.cancel()
        getAnnouncementsJob =
            getAnnouncementsUseCase(courseId)
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
                                    uiState.copy(announcementResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(announcementResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    announcementResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }
}
