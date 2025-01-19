package app.edumate.domain.repository

import app.edumate.domain.model.LoginPreferences
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentSession: UserSession?
    val currentUser: UserInfo?
    val isLoggedIn: Flow<Boolean>
    val signInInfo: Flow<LoginPreferences>

    suspend fun signUpWithEmail(
        fullName: String,
        email: String,
        password: String,
    ): UserInfo?

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): UserInfo?

    suspend fun signInWithGoogle(
        token: String,
        nonce: String,
    ): UserInfo?

    suspend fun resetPasswordForEmail(email: String)

    suspend fun signOut()

    suspend fun saveSignInInfo(
        email: String,
        password: String,
    )

    suspend fun clearSignInInfo()
}
