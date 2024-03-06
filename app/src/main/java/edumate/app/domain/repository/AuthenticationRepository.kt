package edumate.app.domain.repository

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import edumate.app.data.remote.dto.userProfiles.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    /**
     * The ID of the currently signed in user.
     */
    val currentUserId: String

    /**
     * Indicates whether a user is currently signed in.
     */

    val hasUser: Boolean

    /**
     * Returns a flow representing the currently signed in [UserProfile].
     */
    val currentUser: Flow<UserProfile>

    /**
     * Access token (ID token) obtained from Firebase Authentication.
     * This token is used to authenticate requests made to Firebase services on behalf of the user.
     */
    suspend fun getIdToken(): String

    /**
     * Tries to create a new user account with the given email address and password.
     * If successful, it also signs the user in to the app.
     * @param name The name of the user.
     * @param email The email address for the new user account.
     * @param password The password for the new user account.
     * @return An instance of created [UserProfile].
     * @throws FirebaseAuthWeakPasswordException Thrown if the password is not strong enough.
     * @throws FirebaseAuthInvalidCredentialsException Thrown if the email address is malformed.
     * @throws FirebaseAuthUserCollisionException Thrown if there already exists an account with the
     * given email address.
     */
    suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ): UserProfile

    /**
     * Tries to sign in a user with the given email address and password.
     * @param email The email address of the user.
     * @param password The password of the user.
     * @return An instance of signed in [UserProfile].
     * @throws FirebaseAuthInvalidUserException Thrown if the user account corresponding to email
     * does not exist or has been disabled.
     * @throws FirebaseAuthInvalidCredentialsException Thrown if the email or password is invalid.
     */
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): UserProfile

    /**
     * Tries to sign in a user with the given Google authentication token.
     * @param idToken The Google authentication token.
     * @return An instance of signed in [UserProfile].
     * @throws FirebaseAuthInvalidUserException Thrown if the user account you are trying to sign in
     * to has been disabled. Also thrown if credential is an EmailAuthCredential with an email
     * address that does not correspond to an existing user.
     * @throws FirebaseAuthInvalidCredentialsException Thrown if the credential is malformed or has
     * expired.
     * @throws FirebaseAuthUserCollisionException Thrown if there already exists an account with the
     * email address asserted by the credential. Resolve this case by calling
     * fetchSignInMethodsForEmail(String) and then asking the user to sign in using one of them.
     */
    suspend fun signInWithGoogle(idToken: String): UserProfile

    /**
     * Triggers the Firebase Authentication backend to send a password-reset email to the given
     * email address, which must correspond to an existing user of your app.
     * @param email The email address of the account for which to send the password-reset email.
     * @return The email address to which the password reset email was sent.
     * @throws FirebaseAuthInvalidUserException Thrown if there is no user profile corresponding to
     * the given email address.
     */
    suspend fun sendPasswordResetEmail(email: String): String

    /**
     * Signs out the current user and clears it from the disk cache.
     */
    fun signOut()
}
