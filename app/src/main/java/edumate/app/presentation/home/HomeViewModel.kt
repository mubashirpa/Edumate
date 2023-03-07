package edumate.app.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnOpenFabMenuChange -> {
                uiState = uiState.copy(openFabMenu = event.open)
            }
        }
    }
}