package edumate.app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edumate.app.core.FirebaseConstants
import edumate.app.domain.repository.StudentsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class StudentsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentsRepository {

    override suspend fun addStudent(courseId: String, studentId: String): String {
        // Add $uid in courses/$courseId/students array
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION).document(courseId)
            .update(FirebaseConstants.Firestore.STUDENTS, FieldValue.arrayUnion(studentId)).await()
        // After add $courseId in users/$uid/enrolled array
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(studentId)
            .update(FirebaseConstants.Firestore.ENROLLED, FieldValue.arrayUnion(courseId)).await()
        return studentId
    }

    override suspend fun deleteStudent(courseId: String, studentId: String) {
        // Remove $courseId from users/$uid/enrolled array
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(studentId)
            .update(FirebaseConstants.Firestore.ENROLLED, FieldValue.arrayRemove(courseId)).await()
        // After remove $uid from courses/$courseId/students array
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION).document(courseId)
            .update(FirebaseConstants.Firestore.STUDENTS, FieldValue.arrayRemove(studentId)).await()
    }
}