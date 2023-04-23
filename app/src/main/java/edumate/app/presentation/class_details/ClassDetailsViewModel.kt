package edumate.app.presentation.class_details

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
import edumate.app.domain.usecase.courses.GetCourseUseCase
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ClassDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCourseUseCase: GetCourseUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ClassDetailsUiState())
        private set

    private val courseId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.CLASS_DETAILS_COURSE_ID])
    } catch (e: IllegalStateException) {
        null
    }

    init {
        fetchCourse()
    }

    fun onEvent(event: ClassDetailsUiEvent) {
        when (event) {
            ClassDetailsUiEvent.OnRetry -> {
                fetchCourse()
            }
        }
    }

    private fun fetchCourse() {
        if (courseId != null) {
            getCourseUseCase(courseId).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(dataState = DataState.LOADING)
                    }

                    is Resource.Success -> {
                        val course = resource.data
                        uiState = if (course != null) {
                            uiState.copy(
                                course = course,
                                dataState = DataState.SUCCESS
                            )
                        } else {
                            uiState.copy(
                                dataState = DataState.EMPTY(
                                    message = UiText.StringResource(
                                        Strings.error_unexpected
                                    )
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        uiState = uiState.copy(
                            dataState = DataState.ERROR(
                                message = UiText.StringResource(Strings.error_unknown)
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            uiState = uiState.copy(dataState = DataState.ERROR(message = UiText.Empty))
        }
    }
}