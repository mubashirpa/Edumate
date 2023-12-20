package edumate.app.core

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    const val USER_PREFERENCES_NAME = "user_preferences"
    val APP_THEME = stringPreferencesKey("app_theme")
    val APP_LANGUAGE = stringPreferencesKey("app_language")
}
