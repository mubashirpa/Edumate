package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.UserProfileDto
import edumate.app.domain.repository.TeachersRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class TeachersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TeachersRepository {

    override suspend fun addTeacher(courseId: String, teacherId: String): String {
        // Add $uid in courses/$courseId/teachers array
        coursesCollection().document(courseId)
            .update(FirebaseConstants.Firestore.TEACHER_GROUP_ID, FieldValue.arrayUnion(teacherId))
            .await()
        // After add $courseId in users/$uid/teaching array
        usersCollection().document(teacherId)
            .update(FirebaseConstants.Firestore.TEACHING, FieldValue.arrayUnion(courseId)).await()
        return teacherId
    }

    override suspend fun deleteTeacher(courseId: String, teacherId: String) {
        // Remove $courseId from users/$uid/teaching array
        usersCollection().document(teacherId)
            .update(FirebaseConstants.Firestore.TEACHING, FieldValue.arrayRemove(courseId)).await()
        // After remove $uid from courses/$courseId/teachers array
        coursesCollection().document(courseId)
            .update(FirebaseConstants.Firestore.TEACHER_GROUP_ID, FieldValue.arrayRemove(teacherId))
            .await()
    }

    override suspend fun teachers(courseId: String): List<UserProfileDto> {
        return usersCollection().whereArrayContains(FirebaseConstants.Firestore.TEACHING, courseId)
            .get().await().documents.mapNotNull { snapshot ->
                snapshot.toObject<UserProfileDto>()
            }
    }

    private fun coursesCollection(): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)

    private fun usersCollection(): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
}