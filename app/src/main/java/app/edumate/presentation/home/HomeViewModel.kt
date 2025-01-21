package app.edumate.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.courses.GetCoursesUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCoursesUseCase: GetCoursesUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        getCurrentUser()
        getCourses()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnAppBarDropdownExpandedChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            HomeUiEvent.OnRefresh -> {
            }

            is HomeUiEvent.OnShowAddCourseBottomSheetChange -> {
                uiState = uiState.copy(showAddCourseBottomSheet = event.show)
            }

            is HomeUiEvent.OnShowCreateCourseBottomSheetChange -> {
                uiState = uiState.copy(showCreateCourseBottomSheet = event.show)
            }

            is HomeUiEvent.OnShowJoinCourseBottomSheetChange -> {
                uiState = uiState.copy(showJoinCourseBottomSheet = event.show)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    uiState = uiState.copy(currentUser = result.data)
                }
            }.launchIn(viewModelScope)
    }

    private fun getCourses() {
        getCoursesUseCase()
            .onEach { result ->
                uiState = uiState.copy(coursesResult = result)
            }.launchIn(viewModelScope)
    }
}
