package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.model.course_work.CourseWorkState
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class CourseWorkRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseWorkRepository {

    override suspend fun create(courseId: String, courseWorkDto: CourseWorkDto): CourseWorkDto? {
        val id = courseWorkDto.id
        coursesWork(courseId).document(id).set(courseWorkDto.toMap()).await()
        return get(courseId, id)
    }

    override suspend fun delete(courseId: String, id: String) {
        coursesWork(courseId).document(id).delete().await()
    }

    override suspend fun get(courseId: String, id: String): CourseWorkDto? {
        val documentSnapshot = coursesWork(courseId).document(id).get().await()
        return documentSnapshot.toObject<CourseWorkDto>()
    }

    override suspend fun list(courseId: String): List<CourseWorkDto> {
        return coursesWork(courseId)
            .whereEqualTo(FirebaseConstants.Firestore.STATE, CourseWorkState.PUBLISHED)
            .orderBy(FirebaseConstants.Firestore.CREATION_TIME, Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull { snapshot ->
                snapshot.toObject<CourseWorkDto>()
            }
    }

    override suspend fun patch(
        courseId: String,
        id: String,
        courseWorkDto: CourseWorkDto
    ): CourseWorkDto? {
        coursesWork(courseId).document(id).update(courseWorkDto.toMap()).await()
        return get(courseId, id)
    }

    private fun coursesWork(courseId: String): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION).document(courseId)
            .collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION)
}