package edumate.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import edumate.app.core.PreferencesKeys
import edumate.app.core.utils.enumValueOf
import edumate.app.domain.model.UserPreferences
import edumate.app.domain.repository.UserPreferencesRepository
import edumate.app.presentation.settings.AppTheme
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PreferencesKeys.USER_PREFERENCES_NAME
)

class UserPreferencesRepositoryImpl @Inject constructor(
    private val context: Context
) : UserPreferencesRepository {

    override val userPreferencesFlow: Flow<UserPreferences>
        get() = context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val appTheme: AppTheme = enumValueOf(
                preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
            )!!
            val appLanguage: String = preferences[PreferencesKeys.APP_LANGUAGE] ?: "en"
            UserPreferences(
                appTheme = appTheme,
                appLanguage = appLanguage
            )
        }

    override suspend fun changeAppTheme(appTheme: AppTheme) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.APP_THEME] = appTheme.name
        }
    }

    override suspend fun changeAppLanguage(language: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.APP_LANGUAGE] = language
        }
    }
}