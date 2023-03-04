package edumate.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.UsersDto
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseAuthRepository {

    override val currentUserId: String
        get() = firebaseAuth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = firebaseAuth.currentUser != null

    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser)
                }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }

    override suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): FirebaseUser? {
        val user = firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
        user?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(name).build()
        )?.await()
        createUserInFireStore()
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
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val isNewUser = authResult.additionalUserInfo?.isNewUser == true
        if (isNewUser) createUserInFireStore()
        return authResult.user
    }

    override suspend fun sendPasswordResetEmail(email: String): String {
        firebaseAuth.sendPasswordResetEmail(email).await()
        return email
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private suspend fun createUserInFireStore() {
        firebaseAuth.currentUser?.apply {
            val user = UsersDto(
                displayName = displayName,
                emailAddress = email,
                photoUrl = photoUrl?.toString(),
                verified = isEmailVerified
            ).toMap()

            firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(uid)
                .set(user)
                .await()
        }
    }
}