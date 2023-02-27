package edumate.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import edumate.app.core.FirebaseKeys
import edumate.app.data.remote.dto.RoomsDto
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.RoomsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class RoomsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : RoomsRepository {

    override suspend fun rooms(): List<RoomsDto> {
        return firestore.collection(FirebaseKeys.Firestore.ROOMS_COLLECTION).whereArrayContains(
            FirebaseKeys.Firestore.MEMBERS,
            firebaseAuthRepository.currentUserId
        ).get()
            .await().documents.mapNotNull { snapshot ->
                snapshot.toObject(RoomsDto::class.java)
            }
    }

    override suspend fun add(roomsDto: RoomsDto): String {
        return firestore.collection(FirebaseKeys.Firestore.ROOMS_COLLECTION).add(roomsDto.toMap())
            .await().id
    }

    override suspend fun update(roomId: String, roomsDto: RoomsDto) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(roomId: String) {
        TODO("Not yet implemented")
    }
}