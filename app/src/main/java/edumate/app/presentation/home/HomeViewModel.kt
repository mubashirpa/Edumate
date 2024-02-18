package edumate.app.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(HomeUiState())
            private set

        init {
            getCurrentUserUseCase().map { user ->
                uiState = uiState.copy(currentUser = user)
            }.launchIn(viewModelScope)
        }

        fun onEvent(event: HomeUiEvent) {
            uiState =
                when (event) {
                    is HomeUiEvent.OnAppBarDropdownExpandedChange -> {
                        uiState.copy(appBarDropdownExpanded = event.expanded)
                    }

                    is HomeUiEvent.OnRefreshChange -> {
                        uiState.copy(isRefreshing = event.refreshing)
                    }

                    is HomeUiEvent.OnShowAddCourseBottomSheetChange -> {
                        uiState.copy(showAddCourseBottomSheet = event.open)
                    }
                }
        }
    }
