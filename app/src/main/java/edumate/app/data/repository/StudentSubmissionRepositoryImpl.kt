package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.StudentSubmissionDto
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class StudentSubmissionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentSubmissionRepository {

    override suspend fun get(
        courseId: String,
        courseWorkId: String,
        id: String
    ): StudentSubmissionDto? {
        val documentSnapshot = studentSubmissions(courseId, courseWorkId).document(id).get().await()
        return documentSnapshot.toObject<StudentSubmissionDto>()
    }

    override suspend fun list(courseId: String, courseWorkId: String): List<StudentSubmissionDto> {
        return studentSubmissions(courseId, courseWorkId)
            .orderBy(FirebaseConstants.Firestore.CREATION_TIME, Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull { snapshot ->
                snapshot.toObject<StudentSubmissionDto>()
            }
    }

    override suspend fun turnIn(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmissionDto
    ) {
        studentSubmissions(courseId, courseWorkId).document(id).set(studentSubmission.toMap())
            .await()
    }

    private fun studentSubmissions(courseId: String, courseWorkId: String): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION).document(courseId)
            .collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION).document(courseWorkId)
            .collection(FirebaseConstants.Firestore.STUDENT_SUBMISSIONS_COLLECTION)
}