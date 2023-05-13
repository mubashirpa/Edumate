package edumate.app.presentation.settings

sealed class SettingsUiEvent {
    data class OnAppLanguageChange(val index: Int, val appLanguage: String) : SettingsUiEvent()
    data class OnAppThemeChange(val index: Int, val appTheme: AppTheme) : SettingsUiEvent()
}