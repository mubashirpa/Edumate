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
import edumate.app.domain.usecase.announcements.ListAnnouncements
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class StreamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listAnnouncementsUseCase: ListAnnouncements
) : ViewModel() {

    var uiState by mutableStateOf(StreamUiState())
        private set

    private val courseId: String =
        checkNotNull(savedStateHandle[Routes.Args.STREAM_SCREEN_COURSE_ID])
    private var listAnnouncementsJob: Job? = null

    init {
        fetchAnnouncements(false)
    }

    fun onEvent(event: StreamUiEvent) {
        when (event) {
            is StreamUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            StreamUiEvent.OnRefresh -> {
                fetchAnnouncements(true)
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
}