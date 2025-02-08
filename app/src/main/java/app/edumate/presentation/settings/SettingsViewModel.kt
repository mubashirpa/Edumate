package app.edumate.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.domain.model.preferences.AppTheme
import app.edumate.domain.usecase.preferences.ConfigureAppThemeUseCase
import app.edumate.domain.usecase.preferences.GetUserPreferencesUseCase
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val configureAppThemeUseCase: ConfigureAppThemeUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(SettingsUiState())
        private set

    init {
        getUserPreferences()
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.OnAppThemeChange -> {
                configureAppTheme(event.appTheme)
            }
        }
    }

    private fun getUserPreferences() {
        viewModelScope.launch {
            getUserPreferencesUseCase().collect { userPreferences ->
                uiState = uiState.copy(selectedTheme = userPreferences.appTheme)
            }
        }
    }

    private fun configureAppTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            configureAppThemeUseCase(appTheme)
        }
    }
}
