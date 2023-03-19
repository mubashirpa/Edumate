package edumate.app.presentation.classwork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.course_work.GetCourseWorksUseCase
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ClassworkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCourseWorksUseCase: GetCourseWorksUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ClassworkUiState())
        private set

    private val courseId: String? = savedStateHandle[Routes.Args.CLASS_DETAILS_COURSE_ID]

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
        fetCourseWorks(false)
    }

    fun onEvent(event: ClassworkUiEvent) {
        when (event) {
            is ClassworkUiEvent.OnOpenFabMenuChange -> {
                uiState = uiState.copy(openFabMenu = event.open)
            }
            is ClassworkUiEvent.OnRefresh -> {
                fetCourseWorks(true)
            }
            is ClassworkUiEvent.OnRetry -> {
                fetCourseWorks(false)
            }
            is ClassworkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetCourseWorks(refreshing: Boolean) {
        // DataState.LOADING is only used when initial loading and retry.
        // Otherwise show the PullRefreshIndicator using refreshing = true
        // Likewise, DataState.ERROR is only used when initial loading and retry.
        // Otherwise show snackbar by using userMessage
        courseId?.let { id ->
            getCourseWorksUseCase(id).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = if (refreshing) {
                            uiState.copy(refreshing = true)
                        } else {
                            uiState.copy(dataState = DataState.LOADING)
                        }
                    }
                    is Resource.Success -> {
                        val classWorks = resource.data ?: emptyList()
                        uiState = uiState.copy(
                            dataState = if (classWorks.isEmpty()) {
                                DataState.EMPTY(
                                    message = UiText.Empty
                                )
                            } else {
                                DataState.SUCCESS
                            },
                            classWorks = classWorks,
                            refreshing = false
                        )
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
}