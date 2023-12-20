package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.StudentSubmissionDto
import edumate.app.domain.model.student_submissions.AssignmentSubmission
import edumate.app.domain.model.student_submissions.Attachment
import edumate.app.domain.model.student_submissions.SubmissionState
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
            .orderBy(FirebaseConstants.Firestore.CREATION_TIME, Query.Direction.DESCENDING).get()
            .await().documents.mapNotNull { snapshot ->
                snapshot.toObject<StudentSubmissionDto>()
            }
    }

    override suspend fun modifyAttachments(
        courseId: String,
        courseWorkId: String,
        id: String,
        addAttachments: List<Attachment>
    ): StudentSubmissionDto? {
        val assignmentSubmission = AssignmentSubmission(attachments = addAttachments)
        studentSubmissions(courseId, courseWorkId).document(id)
            .update(FirebaseConstants.Firestore.ASSIGNMENT_SUBMISSION, assignmentSubmission).await()
        return get(courseId, courseWorkId, id)
    }

    override suspend fun patch(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmissionDto
    ): StudentSubmissionDto? {
        studentSubmissions(courseId, courseWorkId).document(id).set(studentSubmission.toMap())
            .await()
        return get(courseId, courseWorkId, id)
    }

    override suspend fun reclaim(courseId: String, courseWorkId: String, id: String) {
        studentSubmissions(courseId, courseWorkId).document(id)
            .update(FirebaseConstants.Firestore.STATE, SubmissionState.RECLAIMED_BY_STUDENT).await()
    }

    override suspend fun `return`(courseId: String, courseWorkId: String, id: String) {
        studentSubmissions(courseId, courseWorkId).document(id)
            .update(FirebaseConstants.Firestore.STATE, SubmissionState.RETURNED).await()
    }

    override suspend fun turnIn(courseId: String, courseWorkId: String, id: String) {
        studentSubmissions(courseId, courseWorkId).document(id)
            .update(FirebaseConstants.Firestore.STATE, SubmissionState.TURNED_IN).await()
    }

    private fun studentSubmissions(courseId: String, courseWorkId: String): CollectionReference {
        return firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
            .document(courseId).collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION)
            .document(courseWorkId)
            .collection(FirebaseConstants.Firestore.STUDENT_SUBMISSIONS_COLLECTION)
    }
}