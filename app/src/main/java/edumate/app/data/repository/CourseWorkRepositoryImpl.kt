package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.IndividualStudentsOptions
import edumate.app.domain.model.course_work.CourseWorkState
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class CourseWorkRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseWorkRepository {

    override suspend fun create(courseId: String, courseWorkDto: CourseWorkDto): CourseWorkDto? {
        val id = courseWorkDto.id
        courseWork(courseId).document(id).set(courseWorkDto.toMap()).await()
        return get(courseId, id)
    }

    override suspend fun delete(courseId: String, id: String) {
        courseWork(courseId).document(id).delete().await()
    }

    override suspend fun get(courseId: String, id: String): CourseWorkDto? {
        val documentSnapshot = courseWork(courseId).document(id).get().await()
        return documentSnapshot.toObject<CourseWorkDto>()
    }

    override suspend fun list(
        courseId: String,
        courseWorkState: CourseWorkState,
        orderBy: String,
        pageSize: Int?
    ): List<CourseWorkDto> {
        var query: Query = courseWork(courseId)
        query = query.whereEqualTo(FirebaseConstants.Firestore.STATE, courseWorkState)
        // TODO("Use orderBy")
        query = query.orderBy(FirebaseConstants.Firestore.CREATION_TIME, Query.Direction.DESCENDING)
        if (pageSize != null && pageSize > 0) {
            query = query.limit(pageSize.toLong())
        }
        return query.get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject<CourseWorkDto>()
        }
    }

    override suspend fun modifyAssignees(
        courseId: String,
        id: String,
        assigneeMode: AssigneeMode,
        modifyIndividualStudentsOptions: IndividualStudentsOptions?
    ): CourseWorkDto? {
        TODO("Feature is not available yet")
    }

    override suspend fun patch(
        courseId: String,
        id: String,
        courseWorkDto: CourseWorkDto
    ): CourseWorkDto? {
        courseWork(courseId).document(id).update(courseWorkDto.toMap()).await()
        return get(courseId, id)
    }

    private fun courseWork(courseId: String): CollectionReference {
        return firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
            .document(courseId).collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION)
    }
}