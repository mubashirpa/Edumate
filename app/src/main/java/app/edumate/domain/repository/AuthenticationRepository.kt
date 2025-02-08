package app.edumate.domain.repository

import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession

interface AuthenticationRepository {
    suspend fun currentSession(): UserSession?

    suspend fun currentUser(): UserInfo?

    suspend fun isUserLoggedIn(): Boolean

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

    suspend fun resendSignUpConfirmationEmail(email: String)

    suspend fun resetPasswordForEmail(email: String)

    suspend fun updatePassword(newPassword: String): UserInfo

    suspend fun signOut()
}
