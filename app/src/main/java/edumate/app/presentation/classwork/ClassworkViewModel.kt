package edumate.app.presentation.classwork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Resource
import edumate.app.domain.usecase.course_work.GetCourseWorksUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class ClassworkViewModel @Inject constructor(
    private val getCourseWorksUseCase: GetCourseWorksUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ClassworkUiState())
        private set

    init {
        viewModelScope.launch {
            fetCourseWorks("2Mx7YrbyGUr8tsuBVr4x")
        }
    }

    fun onEvent(event: ClassworkUiEvent) {
        when (event) {
            is ClassworkUiEvent.OnOpenFabMenuChange -> {
                uiState = uiState.copy(openFabMenu = event.open)
            }
        }
    }

    private fun fetCourseWorks(courseId: String) {
        getCourseWorksUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(dataState = DataState.LOADING)
                }
                is Resource.Success -> {
                    val classWorks = resource.data
                    uiState = uiState.copy(
                        dataState = if (classWorks.isNullOrEmpty()) DataState.EMPTY else DataState.SUCCESS,
                        classWorks = classWorks ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    uiState = uiState.copy(
                        dataState = DataState.ERROR,
                        errorMessage = resource.message!!
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}