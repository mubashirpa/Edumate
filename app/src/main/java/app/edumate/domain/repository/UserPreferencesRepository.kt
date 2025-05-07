package app.edumate.domain.repository

import app.edumate.domain.model.preferences.AppTheme
import app.edumate.domain.model.preferences.LoginPreferences
import app.edumate.domain.model.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    val loginPreferencesFlow: Flow<LoginPreferences>

    suspend fun configureAppTheme(appTheme: AppTheme)

    suspend fun updateReviewDialogShownTime(lastReviewShownAt: Long)

    suspend fun configureLoginPreferences(
        email: String,
        password: String,
    )

    suspend fun clearLoginPreferences()
}
