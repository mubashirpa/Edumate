package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CourseDto
import edumate.app.domain.model.courses.CourseState
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class CoursesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CoursesRepository {

    override suspend fun create(courseDto: CourseDto): CourseDto? {
        val id = courseDto.id
        coursesCollection().document(id).set(courseDto.toMap()).await()
        // After a course created, add $courseId in users/$uid/teaching array
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION)
            .document(courseDto.ownerId)
            .update(FirebaseConstants.Firestore.TEACHING, FieldValue.arrayUnion(id)).await()
        return get(id)
    }

    override suspend fun delete(id: String) {
        coursesCollection().document(id).delete().await()
    }

    override suspend fun get(id: String): CourseDto? {
        val documentSnapshot = coursesCollection().document(id).get().await()
        return documentSnapshot.toObject<CourseDto>()
    }

    override suspend fun list(
        studentId: String?,
        teacherId: String?,
        courseState: CourseState,
        pageSize: Int?
    ): List<CourseDto> {
        // TODO("Use courseState and pageSize")
        return when {
            studentId != null -> {
                coursesCollection().whereArrayContains(
                    FirebaseConstants.Firestore.COURSE_GROUP_ID,
                    studentId
                ).get().await().documents.mapNotNull { snapshot ->
                    snapshot.toObject<CourseDto>()
                }
            }

            teacherId != null -> {
                coursesCollection().whereArrayContains(
                    FirebaseConstants.Firestore.TEACHER_GROUP_ID,
                    teacherId
                ).get().await().documents.mapNotNull { snapshot ->
                    snapshot.toObject<CourseDto>()
                }
            }

            else -> {
                emptyList()
            }
        }
    }

    override suspend fun update(id: String, courseDto: CourseDto) {
        coursesCollection().document(id).update(courseDto.toMap()).await()
    }

    private fun coursesCollection(): CollectionReference {
        return firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
    }
}