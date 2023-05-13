package edumate.app.domain.model

import edumate.app.presentation.settings.AppTheme

data class UserPreferences(
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val appLanguage: String = "en"
)