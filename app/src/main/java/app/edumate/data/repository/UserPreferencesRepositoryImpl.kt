package app.edumate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import app.edumate.core.PreferencesKeys
import app.edumate.domain.model.preferences.AppTheme
import app.edumate.domain.model.preferences.LoginPreferences
import app.edumate.domain.model.preferences.UserPreferences
import app.edumate.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {
    override val userPreferencesFlow: Flow<UserPreferences>
        get() =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map { preferences ->
                    val appTheme =
                        preferences[PreferencesKeys.APP_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
                    UserPreferences(
                        appTheme = enumValueOf(appTheme),
                    )
                }

    override val loginPreferencesFlow: Flow<LoginPreferences>
        get() =
            dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map { preferences ->
                    val email = preferences[PreferencesKeys.USER_EMAIL].orEmpty()
                    val password = preferences[PreferencesKeys.USER_PASSWORD].orEmpty()
                    LoginPreferences(
                        email = email,
                        password = password,
                    )
                }

    override suspend fun configureAppTheme(appTheme: AppTheme) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.APP_THEME] = appTheme.name
        }
    }

    override suspend fun configureLoginPreferences(
        email: String,
        password: String,
    ) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.USER_EMAIL] = email
            settings[PreferencesKeys.USER_PASSWORD] = password
        }
    }

    override suspend fun clearLoginPreferences() {
        configureLoginPreferences("", "")
    }
}
