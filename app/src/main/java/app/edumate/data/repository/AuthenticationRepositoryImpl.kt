package app.edumate.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import app.edumate.core.PreferencesKeys
import app.edumate.data.local.dataStore
import app.edumate.domain.model.LoginPreferences
import app.edumate.domain.repository.AuthenticationRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthenticationRepositoryImpl(
    private val auth: Auth,
    private val context: Context,
) : AuthenticationRepository {
    override val signInInfo: Flow<LoginPreferences>
        get() =
            context.dataStore.data
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

    override suspend fun currentSession(): UserSession? {
        auth.awaitInitialization()
        return auth.currentSessionOrNull()
    }

    override suspend fun currentUser(): UserInfo? = currentSession()?.user

    override suspend fun isUserLoggedIn(): Boolean = currentUser() != null

    override suspend fun signUpWithEmail(
        fullName: String,
        email: String,
        password: String,
    ): UserInfo? =
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data =
                buildJsonObject {
                    put("full_name", fullName)
                }
        }

    override suspend fun signInWithEmail(
        email: String,
        password: String,
    ): UserInfo? {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return currentUser()
    }

    override suspend fun signInWithGoogle(
        token: String,
        nonce: String,
    ): UserInfo? {
        auth.signInWith(IDToken) {
            idToken = token
            provider = Google
            this.nonce = nonce
        }
        return currentUser()
    }

    override suspend fun resetPasswordForEmail(email: String) {
        auth.resetPasswordForEmail(email = email)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun saveSignInInfo(
        email: String,
        password: String,
    ) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USER_EMAIL] = email
            settings[PreferencesKeys.USER_PASSWORD] = password
        }
    }

    override suspend fun clearSignInInfo() {
        saveSignInInfo("", "")
    }
}
