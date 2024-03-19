package edumate.app.presentation.stream

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.announcements.DeleteAnnouncementUseCase
import edumate.app.domain.usecase.classroom.announcements.ListAnnouncementsUseCase
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StreamViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
        private val listAnnouncementsUseCase: ListAnnouncementsUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(StreamUiState())
            private set

        private val courseId: String =
            checkNotNull(savedStateHandle[Routes.Args.STREAM_SCREEN_COURSE_ID])
        private var listAnnouncementsJob: Job? = null

        init {
            getCurrentUserUseCase().onEach {
                uiState = uiState.copy(user = it)
            }.launchIn(viewModelScope)
            listAnnouncements(false)
        }

        fun onEvent(event: StreamUiEvent) {
            when (event) {
                is StreamUiEvent.OnAppBarDropdownExpandedChange -> {
                    uiState = uiState.copy(appBarDropdownExpanded = event.expanded)
                }

                is StreamUiEvent.OnDeleteAnnouncement -> {
                    deleteAnnouncement(event.id)
                }

                is StreamUiEvent.OnOpenDeleteAnnouncementDialogChange -> {
                    uiState = uiState.copy(deleteAnnouncementId = event.id)
                }

                StreamUiEvent.OnRefresh -> {
                    listAnnouncements(true)
                }

                StreamUiEvent.OnRetry -> {
                    listAnnouncements(false)
                }

                StreamUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun listAnnouncements(isRefreshing: Boolean) {
            // Cancel any ongoing listAnnouncementsJob before making a new call.
            listAnnouncementsJob?.cancel()
            listAnnouncementsJob =
                listAnnouncementsUseCase(
                    courseId = courseId,
                    orderBy = "creationTime desc",
                ).onEach { result ->
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
                                    uiState.copy(announcementsResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(announcementsResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    announcementsResult = result,
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        private fun deleteAnnouncement(id: String) {
            deleteAnnouncementUseCase(courseId, id).onEach { result ->
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
                                deleteAnnouncementId = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        listAnnouncements(true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
