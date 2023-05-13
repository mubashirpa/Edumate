package edumate.app.domain.repository

import edumate.app.domain.model.UserPreferences
import edumate.app.presentation.settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun changeAppTheme(appTheme: AppTheme)

    suspend fun changeAppLanguage(language: String)
}