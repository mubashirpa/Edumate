package edumate.app.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var uiState by mutableStateOf(MainUiState())
        private set

    init {
        uiState = uiState.copy(isLoggedIn = firebaseAuthRepository.hasUser)
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collectLatest {
                val appTheme = it.appTheme
                val appLanguage = it.appLanguage
                uiState = uiState.copy(
                    appTheme = appTheme,
                    appLanguage = appLanguage
                )
            }
        }
    }
}