package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class CourseWorkRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseWorkRepository {

    override suspend fun create(courseWorkDto: CourseWorkDto): String {
        val courseId = courseWorkDto.courseId
        val documentId = coursesWorkCollection(courseId).document().id
        val courseWork = courseWorkDto.copy(id = documentId).toMap()
        coursesWorkCollection(courseId).document(documentId).set(courseWork).await()
        return documentId
    }

    override suspend fun delete(courseWorkId: String, courseId: String) {
        coursesWorkCollection(courseId).document(courseWorkId).delete().await()
    }

    override suspend fun get(courseWorkId: String, courseId: String): CourseWorkDto? {
        val documentSnapshot = coursesWorkCollection(courseId).document(courseWorkId).get().await()
        return documentSnapshot.toObject<CourseWorkDto>()
    }

    override suspend fun list(courseId: String): List<CourseWorkDto> {
        return coursesWorkCollection(courseId).orderBy(
            FirebaseConstants.Firestore.CREATION_TIME,
            Query.Direction.DESCENDING
        ).get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject<CourseWorkDto>()
        }
    }

    override suspend fun update(courseWorkDto: CourseWorkDto, courseWorkId: String) {
        coursesWorkCollection(courseWorkDto.courseId).document(courseWorkId)
            .update(courseWorkDto.toMap()).await()
    }

    private fun coursesWorkCollection(courseId: String): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
            .document(courseId).collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION)
}