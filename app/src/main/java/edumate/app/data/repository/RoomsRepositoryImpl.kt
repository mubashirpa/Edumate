package edumate.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.RoomsDto
import edumate.app.domain.repository.RoomsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class RoomsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RoomsRepository {

    override suspend fun rooms(uid: String): List<RoomsDto> {
        return firestore.collection(FirebaseConstants.Firestore.ROOMS_COLLECTION)
            .whereArrayContains(FirebaseConstants.Firestore.TEACHERS, uid).get()
            .await().documents.mapNotNull { snapshot ->
                snapshot.toObject(RoomsDto::class.java)
            }
    }

    override suspend fun add(roomsDto: RoomsDto): String {
        return firestore.collection(FirebaseConstants.Firestore.ROOMS_COLLECTION)
            .add(roomsDto.toMap()).await().id
    }

    override suspend fun update(roomId: String, roomsDto: RoomsDto) {
        firestore.collection(FirebaseConstants.Firestore.ROOMS_COLLECTION).document(roomId)
            .update(roomsDto.toMap()).await()
    }

    override suspend fun delete(roomId: String) {
        firestore.collection(FirebaseConstants.Firestore.ROOMS_COLLECTION).document(roomId).delete()
            .await()
    }
}