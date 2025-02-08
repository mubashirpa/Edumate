package app.edumate.presentation.settings

import app.edumate.domain.model.preferences.AppTheme

data class SettingsUiState(
    val selectedTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
)
