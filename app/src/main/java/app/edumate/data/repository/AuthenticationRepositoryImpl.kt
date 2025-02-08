package app.edumate.data.repository

import app.edumate.core.Authentication
import app.edumate.domain.repository.AuthenticationRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthenticationRepositoryImpl(
    private val auth: Auth,
) : AuthenticationRepository {
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
                    put(Authentication.Metadata.NAME, fullName)
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

    override suspend fun resendSignUpConfirmationEmail(email: String) {
        auth.resendEmail(OtpType.Email.SIGNUP, email)
    }

    override suspend fun resetPasswordForEmail(email: String) {
        auth.resetPasswordForEmail(email = email)
    }

    override suspend fun updatePassword(newPassword: String): UserInfo =
        auth.updateUser {
            password = newPassword
        }

    override suspend fun signOut() {
        auth.signOut()
    }
}
