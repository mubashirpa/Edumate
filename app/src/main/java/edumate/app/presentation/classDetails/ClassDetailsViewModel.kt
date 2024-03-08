package edumate.app.presentation.classDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.usecase.classroom.courses.GetCourseUseCase
import edumate.app.navigation.Routes
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ClassDetailsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getCourseUseCase: GetCourseUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(ClassDetailsUiState())
            private set

        private val courseId: String =
            checkNotNull(savedStateHandle[Routes.Args.CLASS_DETAILS_COURSE_ID])

        init {
            getCourse(courseId)
        }

        fun onEvent(event: ClassDetailsUiEvent) {
            when (event) {
                ClassDetailsUiEvent.Retry -> {
                    getCourse(courseId)
                }
            }
        }

        private fun getCourse(id: String) {
            getCourseUseCase(id).onEach { result ->
                uiState = uiState.copy(courseResult = result)
            }.launchIn(viewModelScope)
        }
    }
