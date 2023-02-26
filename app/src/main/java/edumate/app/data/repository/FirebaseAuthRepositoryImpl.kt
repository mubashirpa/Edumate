package edumate.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthRepository {

    override val hasUser: Boolean
        get() = firebaseAuth.currentUser != null

    override suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): FirebaseUser? {
        val user = firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
        user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(name).build()
        )?.await()
        return user
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser? {
        return firebaseAuth.signInWithEmailAndPassword(email, password).await().user
    }

    override suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(firebaseCredential).await().user
    }

    override suspend fun sendPasswordResetEmail(email: String): String {
        firebaseAuth.sendPasswordResetEmail(email).await()
        return email
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}