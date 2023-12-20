package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.UserProfileDto
import edumate.app.domain.repository.StudentsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class StudentsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentsRepository {

    override suspend fun create(courseId: String, userId: String) {
        // Add $uid in courses/$courseId/students array
        courses().document(courseId)
            .update(FirebaseConstants.Firestore.COURSE_GROUP_ID, FieldValue.arrayUnion(userId))
            .await()
        // After add $courseId in users/$uid/enrolled array
        users().document(userId)
            .update(FirebaseConstants.Firestore.ENROLLED, FieldValue.arrayUnion(courseId)).await()
    }

    override suspend fun delete(courseId: String, userId: String) {
        // Remove $courseId from users/$uid/enrolled array
        users().document(userId)
            .update(FirebaseConstants.Firestore.ENROLLED, FieldValue.arrayRemove(courseId)).await()
        // After remove $uid from courses/$courseId/students array
        courses().document(courseId)
            .update(FirebaseConstants.Firestore.COURSE_GROUP_ID, FieldValue.arrayRemove(userId))
            .await()
    }

    override suspend fun get(courseId: String, userId: String): UserProfileDto {
        TODO("Not yet implemented")
    }

    override suspend fun list(courseId: String, pageSize: Int?): List<UserProfileDto> {
        var query: Query = users()
        query = query.whereArrayContains(FirebaseConstants.Firestore.ENROLLED, courseId)
        query = if (pageSize != null && pageSize > 0) {
            query.limit(pageSize.toLong())
        } else {
            query.limit(30)
        }
        return query.get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject<UserProfileDto>()
        }
    }

    private fun courses(): CollectionReference {
        return firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
    }

    private fun users(): CollectionReference {
        return firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
    }
}