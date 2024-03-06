package edumate.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import edumate.app.core.Server
import edumate.app.data.remote.dto.userProfiles.Name
import edumate.app.data.remote.dto.userProfiles.UserProfile
import edumate.app.domain.repository.AuthenticationRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val httpClient: HttpClient,
    ) : AuthenticationRepository {
        override val currentUserId: String
            get() = firebaseAuth.currentUser?.uid.orEmpty()

        override val hasUser: Boolean
            get() = firebaseAuth.currentUser != null

        override val currentUser: Flow<UserProfile>
            get() =
                callbackFlow {
                    val listener =
                        FirebaseAuth.AuthStateListener { auth ->
                            this.trySend(
                                auth.currentUser?.let {
                                    UserProfile(
                                        emailAddress = it.email,
                                        id = it.uid,
                                        name = Name(fullName = it.displayName),
                                        photoUrl = it.photoUrl.toString(),
                                        verified = it.isEmailVerified,
                                    )
                                } ?: UserProfile(),
                            )
                        }
                    firebaseAuth.addAuthStateListener(listener)
                    awaitClose { firebaseAuth.removeAuthStateListener(listener) }
                }

        override suspend fun getIdToken(): String {
            val user = firebaseAuth.currentUser
            val task = user?.getIdToken(true)?.await()
            return task?.token.orEmpty()
        }

        override suspend fun createUserWithEmailAndPassword(
            name: String,
            email: String,
            password: String,
        ): UserProfile {
            val user = firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
            user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                ?.await()
            createUserInDatabase()
            return UserProfile(
                emailAddress = user?.email,
                id = user?.uid,
                name = Name(fullName = user?.displayName),
                photoUrl = user?.photoUrl.toString(),
                verified = user?.isEmailVerified,
            )
        }

        override suspend fun signInWithEmailAndPassword(
            email: String,
            password: String,
        ): UserProfile {
            val user = firebaseAuth.signInWithEmailAndPassword(email, password).await().user
            return UserProfile(
                emailAddress = user?.email,
                id = user?.uid,
                name = Name(fullName = user?.displayName),
                photoUrl = user?.photoUrl.toString(),
                verified = user?.isEmailVerified,
            )
        }

        override suspend fun signInWithGoogle(idToken: String): UserProfile {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            if (authResult.additionalUserInfo?.isNewUser == true) {
                createUserInDatabase()
            }
            val user = authResult.user
            return UserProfile(
                emailAddress = user?.email,
                id = user?.uid,
                name = Name(fullName = user?.displayName),
                photoUrl = user?.photoUrl.toString(),
                verified = user?.isEmailVerified,
            )
        }

        override suspend fun sendPasswordResetEmail(email: String): String {
            firebaseAuth.sendPasswordResetEmail(email).await()
            return email
        }

        override fun signOut() {
            firebaseAuth.signOut()
        }

        private suspend fun createUserInDatabase() {
            firebaseAuth.currentUser?.apply {
                val firstAndLastName = extractFirstAndLastName(displayName.orEmpty())
                val user =
                    UserProfile(
                        emailAddress = email,
                        id = uid,
                        name =
                            Name(
                                firstName = firstAndLastName.first,
                                fullName = displayName,
                                lastName = firstAndLastName.second,
                            ),
                        photoUrl = photoUrl?.toString(),
                        verified = isEmailVerified,
                    )

                httpClient.post(Server.API_BASE_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(user)
                }
            }
        }

        private fun extractFirstAndLastName(fullName: String): Pair<String, String> {
            val parts = fullName.split(" ").filter { it.isNotEmpty() }
            val firstName = parts.firstOrNull() ?: ""
            val lastName =
                if (parts.size > 1) {
                    parts.subList(1, parts.size).joinToString(" ")
                } else {
                    ""
                }
            return Pair(firstName, lastName)
        }
    }
