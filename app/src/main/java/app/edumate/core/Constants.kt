package app.edumate.core

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val BACKDROP_GET_STARTED =
        "https://firebasestorage.googleapis.com/v0/b/edu-mate-app.appspot.com/o/get_started.jpg?alt=media&token=6b9e6215-c0a4-4046-a15d-42cb5a102986"
    const val BACKDROP_GET_STARTED_LOCAL = "file:///android_asset/images/get_started.png"
    const val WEB_GOOGLE_CLIENT_ID =
        "242656945367-ipk4n5tmvqgt9p8kqb9bk0dckgc8lefn.apps.googleusercontent.com"
}

object PreferencesKeys {
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PASSWORD = stringPreferencesKey("user_password")
}

object Authentication {
    object Metadata {
        const val NAME = "name"
        const val AVATAR_URL = "avatar_url"
        const val EMAIL_VERIFIED = "email_verified"
    }
}

object Supabase {
    object Table {
        const val COURSES = "courses"
        const val MEMBERS = "members"
        const val USERS = "users"
    }

    object Column {
        const val USER_ID = "userId"
        const val ID = "id"
        const val NAME = "name"
        const val SECTION = "section"
        const val ROOM = "room"
        const val SUBJECT = "subject"
        const val DISPLAY_NAME = "displayName"
        const val PHOTO_URL = "photoUrl"
    }
}
