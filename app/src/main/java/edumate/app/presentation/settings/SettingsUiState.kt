package edumate.app.presentation.settings

data class SettingsUiState(
    val selectedLanguage: String = "en",
    val selectedTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
)
