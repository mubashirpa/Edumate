package edumate.app.presentation

import edumate.app.presentation.settings.AppTheme

data class MainUiState(
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val appLanguage: String = "en",
    val isLoggedIn: Boolean = false
)