package app.edumate.core

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val BACKDROP_GET_STARTED =
        "https://firebasestorage.googleapis.com/v0/b/edu-mate-app.appspot.com/o/get_started.jpg?alt=media&token=6b9e6215-c0a4-4046-a15d-42cb5a102986"
    const val BACKDROP_GET_STARTED_LOCAL = "file:///android_asset/images/get_started.png"
    const val WEB_GOOGLE_CLIENT_ID =
        "397578092741-alqtcebud1r0tsddkm90gj3bfjebkdk0.apps.googleusercontent.com"
}

object PreferencesKeys {
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PASSWORD = stringPreferencesKey("user_password")
}
