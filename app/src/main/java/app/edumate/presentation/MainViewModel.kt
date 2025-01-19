package app.edumate.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.domain.usecase.authentication.IsUserLoggedInUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    isUserLoggedInUseCase: IsUserLoggedInUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(MainUiState())
        private set

    init {
        viewModelScope.launch {
            uiState =
                uiState.copy(
                    isUserLoggedIn = isUserLoggedInUseCase(),
                    isLoading = false,
                )
        }
    }
}
