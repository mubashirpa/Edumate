package edumate.app.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val userPreferencesRepository: UserPreferencesRepository,
    ) : ViewModel() {
        var uiState by mutableStateOf(SettingsUiState())
            private set

        init {
            viewModelScope.launch {
                userPreferencesRepository.userPreferencesFlow.collectLatest {
                    uiState =
                        uiState.copy(
                            selectedLanguage = it.appLanguage,
                            selectedTheme = it.appTheme,
                        )
                }
            }
        }

        fun onEvent(event: SettingsUiEvent) {
            when (event) {
                is SettingsUiEvent.OnAppLanguageChange -> {
                    changeAppLanguage(event.appLanguage)
                }

                is SettingsUiEvent.OnAppThemeChange -> {
                    changeAppTheme(event.appTheme)
                }
            }
        }

        private fun changeAppTheme(appTheme: AppTheme) {
            viewModelScope.launch {
                userPreferencesRepository.changeAppTheme(appTheme)
            }
        }

        private fun changeAppLanguage(appLanguage: String) {
            viewModelScope.launch {
                userPreferencesRepository.changeAppLanguage(appLanguage)
            }
        }
    }
