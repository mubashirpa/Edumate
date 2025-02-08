package app.edumate.presentation.settings

import app.edumate.domain.model.preferences.AppTheme

sealed class SettingsUiEvent {
    data class OnAppThemeChange(
        val appTheme: AppTheme,
    ) : SettingsUiEvent()
}
