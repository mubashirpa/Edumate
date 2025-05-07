package app.edumate.domain.model.preferences

data class UserPreferences(
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val lastReviewShownAt: Long = 0,
)
