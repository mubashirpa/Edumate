package edumate.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edumate.app.core.FirebaseConstants
import edumate.app.domain.repository.PeoplesRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class PeoplesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PeoplesRepository {

    override suspend fun students(roomId: String) {
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
            .whereArrayContains(FirebaseConstants.Firestore.STUDENTS, roomId).get()
            .await().documents.mapNotNull { snapshot ->
                Log.d("hello", snapshot.toString())
            }
    }

    override suspend fun teachers(roomId: String) {
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
            .whereArrayContains(FirebaseConstants.Firestore.TEACHERS, roomId).get()
            .await().documents.mapNotNull { snapshot ->
                Log.d("hello", snapshot.toString())
            }
    }

    override suspend fun addStudentInRoom(roomId: String, uid: String) {
        firestore.collection(FirebaseConstants.Firestore.ROOMS_COLLECTION).document(roomId)
            .update(FirebaseConstants.Firestore.STUDENTS, FieldValue.arrayUnion(uid))
            .await()
    }

    override suspend fun addStudentInUser(uid: String, roomId: String) {
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(uid)
            .update(FirebaseConstants.Firestore.STUDENT, FieldValue.arrayUnion(roomId))
            .await()
    }

    override suspend fun addTeacherInRoom(roomId: String, uid: String) {
        firestore.collection(FirebaseConstants.Firestore.ROOMS_COLLECTION).document(roomId)
            .update(FirebaseConstants.Firestore.TEACHERS, FieldValue.arrayUnion(uid))
            .await()
    }

    override suspend fun addTeacherInUser(uid: String, roomId: String) {
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(uid)
            .update(FirebaseConstants.Firestore.TEACHER, FieldValue.arrayUnion(roomId))
            .await()
    }

    override suspend fun deleteStudentInUser(uid: String, roomId: String) {
        // TODO("Not tested yet")
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(uid)
            .update(FirebaseConstants.Firestore.STUDENT, FieldValue.arrayRemove(roomId))
            .await()
    }

    override suspend fun deleteTeacherInUser(uid: String, roomId: String) {
        // TODO("Not tested yet")
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(uid)
            .update(FirebaseConstants.Firestore.TEACHER, FieldValue.arrayRemove(roomId))
            .await()
    }
}