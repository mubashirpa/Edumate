package edumate.app.presentation.view_classwork

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.course_work.GetCourseWorkUseCase
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ViewClassworkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCourseWorkUseCase: GetCourseWorkUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ViewClassworkUiState())
        private set

    private val courseWorkId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_WORK_ID])
    } catch (e: IllegalStateException) {
        null
    }
    private val courseId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_COURSE_ID])
    } catch (e: IllegalStateException) {
        null
    }

    init {
        Log.d("hello", "$courseWorkId, $courseId")
        if (courseWorkId != null && courseId != null) {
            getCourseWorkUseCase(courseWorkId, courseId).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d("hello", "Loading")
                    }
                    is Resource.Success -> {
                        val classwork = resource.data
                        uiState = if (classwork != null) {
                            uiState.copy(classwork = classwork)
                        } else {
                            uiState.copy(
                                error = UiText.DynamicString(
                                    "Cannot retrieve Classwork at this time, Please try again later."
                                )
                            )
                        }
                        Log.d("hello", classwork.toString())
                    }
                    is Resource.Error -> {
                        uiState = uiState.copy(userMessage = resource.message)
                        Log.d("hello", resource.message.toString())
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}