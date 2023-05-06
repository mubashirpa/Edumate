package edumate.app.presentation.stream

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.announcements.DeleteAnnouncement
import edumate.app.domain.usecase.announcements.ListAnnouncements
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class StreamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val listAnnouncementsUseCase: ListAnnouncements,
    private val deleteAnnouncementUseCase: DeleteAnnouncement
) : ViewModel() {

    var uiState by mutableStateOf(StreamUiState())
        private set

    private val courseId: String =
        checkNotNull(savedStateHandle[Routes.Args.STREAM_SCREEN_COURSE_ID])
    private var listAnnouncementsJob: Job? = null

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
        fetchAnnouncements(false)
    }

    fun onEvent(event: StreamUiEvent) {
        when (event) {
            is StreamUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            is StreamUiEvent.OnDeleteAnnouncement -> {
                deleteAnnouncement(event.announcementId)
            }

            is StreamUiEvent.OnOpenDeleteAnnouncementDialogChange -> {
                uiState = uiState.copy(deleteAnnouncementId = event.announcementId)
            }

            StreamUiEvent.OnRefresh -> {
                fetchAnnouncements(true)
            }

            StreamUiEvent.OnRetry -> {
                fetchAnnouncements(false)
            }

            StreamUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchAnnouncements(refreshing: Boolean) {
        listAnnouncementsJob?.cancel()
        listAnnouncementsJob = listAnnouncementsUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val announcements = resource.data
                    uiState = if (announcements.isNullOrEmpty()) {
                        uiState.copy(
                            dataState = DataState.EMPTY(
                                UiText.StringResource(Strings.start_a_conversation_with_your_class)
                            ),
                            refreshing = false
                        )
                    } else {
                        uiState.copy(
                            announcements = announcements,
                            dataState = DataState.SUCCESS,
                            refreshing = false
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = if (refreshing) {
                        uiState.copy(
                            refreshing = false,
                            userMessage = resource.message
                        )
                    } else {
                        uiState.copy(dataState = DataState.ERROR(message = resource.message!!))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteAnnouncement(id: String) {
        deleteAnnouncementUseCase(courseId, id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        deleteAnnouncementId = null,
                        openProgressDialog = true
                    )
                }

                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchAnnouncements(true)
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}