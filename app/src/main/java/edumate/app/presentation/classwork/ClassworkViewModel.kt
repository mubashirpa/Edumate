package edumate.app.presentation.classwork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.course_work.DeleteCourseWork
import edumate.app.domain.usecase.course_work.ListCourseWorks
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ClassworkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val listCourseWorksUseCase: ListCourseWorks,
    private val deleteCourseWorkUseCase: DeleteCourseWork
) : ViewModel() {

    var uiState by mutableStateOf(ClassworkUiState())
        private set

    private val courseId: String = checkNotNull(savedStateHandle[Routes.Args.CLASSWORK_COURSE_ID])
    private var listCourseWorksJob: Job? = null

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
        fetchClasswork(false)
    }

    fun onEvent(event: ClassworkUiEvent) {
        when (event) {
            is ClassworkUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            is ClassworkUiEvent.OnDeleteClasswork -> {
                deleteClasswork(event.classworkId)
            }

            is ClassworkUiEvent.OnOpenDeleteClassworkDialogChange -> {
                uiState = uiState.copy(deleteClasswork = event.classwork)
            }

            is ClassworkUiEvent.OnOpenFabMenuChange -> {
                uiState = uiState.copy(openFabMenu = event.open)
            }

            ClassworkUiEvent.OnRefresh -> {
                fetchClasswork(true)
            }

            ClassworkUiEvent.OnRetry -> {
                fetchClasswork(false)
            }

            ClassworkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchClasswork(refreshing: Boolean) {
        // DataState.LOADING is only used when initial loading and retry
        // Otherwise show the PullRefreshIndicator using refreshing = true
        // Likewise, DataState.ERROR is only used when initial loading and retry
        // Otherwise show snackbar by using userMessage.
        // Cancel ongoing getCourseWorksJob before recall.
        listCourseWorksJob?.cancel()
        listCourseWorksJob = listCourseWorksUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val classwork = resource.data
                    uiState = if (classwork.isNullOrEmpty()) {
                        uiState.copy(
                            // Message is set from ui
                            dataState = DataState.EMPTY(UiText.Empty),
                            refreshing = false
                        )
                    } else {
                        uiState.copy(
                            classwork = classwork,
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

    private fun deleteClasswork(id: String) {
        deleteCourseWorkUseCase(courseId, id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        deleteClasswork = null,
                        openProgressDialog = true
                    )
                }

                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchClasswork(true)
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