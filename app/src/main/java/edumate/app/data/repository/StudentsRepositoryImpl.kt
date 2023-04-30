package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.UserProfileDto
import edumate.app.domain.repository.StudentsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class StudentsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentsRepository {

    override suspend fun addStudent(courseId: String, studentId: String): String {
        // Add $uid in courses/$courseId/students array
        coursesCollection().document(courseId)
            .update(FirebaseConstants.Firestore.COURSE_GROUP_ID, FieldValue.arrayUnion(studentId))
            .await()
        // After add $courseId in users/$uid/enrolled array
        usersCollection().document(studentId)
            .update(FirebaseConstants.Firestore.ENROLLED, FieldValue.arrayUnion(courseId)).await()
        return studentId
    }

    override suspend fun deleteStudent(courseId: String, studentId: String) {
        // Remove $courseId from users/$uid/enrolled array
        usersCollection().document(studentId)
            .update(FirebaseConstants.Firestore.ENROLLED, FieldValue.arrayRemove(courseId)).await()
        // After remove $uid from courses/$courseId/students array
        coursesCollection().document(courseId)
            .update(FirebaseConstants.Firestore.COURSE_GROUP_ID, FieldValue.arrayRemove(studentId))
            .await()
    }

    override suspend fun students(courseId: String): List<UserProfileDto> {
        return usersCollection().whereArrayContains(FirebaseConstants.Firestore.ENROLLED, courseId)
            .get().await().documents.mapNotNull { snapshot ->
                snapshot.toObject<UserProfileDto>()
            }
    }

    private fun coursesCollection(): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)

    private fun usersCollection(): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
}