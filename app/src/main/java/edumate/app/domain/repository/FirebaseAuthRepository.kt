package edumate.app.domain.repository

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {

    val currentUserId: String

    val hasUser: Boolean

    /**
     * @return The currently signed-in [FirebaseUser] or null if there is none.
     */
    val currentUser: Flow<FirebaseUser?>

    /**
     * Tries to create a new userProfile account with the given email address and password. If successful, it also signs the userProfile in into the app.
     */
    suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): FirebaseUser?

    /**
     * Tries to sign in a userProfile with the given email address and password.
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser?

    /**
     * Tries to sign in a userProfile with the given AuthCredential.
     * @throws FirebaseAuthInvalidUserException thrown if the userProfile account you are trying to sign in to has been disabled. Also thrown if credential is an EmailAuthCredential with an email address that does not correspond to an existing userProfile.
     * @throws FirebaseAuthInvalidCredentialsException thrown if the credential is malformed or has expired. If credential instance of EmailAuthCredential it will be thrown if the password is incorrect.
     * @throws FirebaseAuthUserCollisionException thrown if there already exists an account with the email address asserted by the credential. Resolve this case by calling fetchSignInMethodsForEmail(String) and then asking the userProfile to sign in using one of them.
     */
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?

    /**
     * Triggers the Firebase Authentication backend to send a password-reset email to the given email address, which must correspond to an existing userProfile of your app.
     * @param email the email of the account to which you wish to issue an account reset email.
     * @throws FirebaseAuthInvalidUserException thrown if there is no userProfile corresponding to the given email address.
     */
    suspend fun sendPasswordResetEmail(email: String): String

    /**
     * Signs out the current userProfile and clears it from the disk cache.
     */
    fun signOut()
}