package edumate.app.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.CoursesDto
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class CoursesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CoursesRepository {

    override suspend fun createCourse(coursesDto: CoursesDto, uid: String): String {
        val documentId = coursesCollection().document().id
        val course = coursesDto.copy(id = documentId).toMap()
        coursesCollection().document(documentId).set(course).await()
        // After a course created, add $courseId in users/$uid/teaching array
        firestore.collection(FirebaseConstants.Firestore.USERS_COLLECTION).document(uid)
            .update(FirebaseConstants.Firestore.TEACHING, FieldValue.arrayUnion(documentId)).await()
        return documentId
    }

    override suspend fun deleteCourse(courseId: String) {
        coursesCollection().document(courseId).delete().await()
    }

    override suspend fun getCourse(courseId: String): CoursesDto? {
        val documentSnapshot = coursesCollection().document(courseId).get().await()
        return documentSnapshot.toObject<CoursesDto>()
    }

    override suspend fun enrolledCourses(studentId: String): List<CoursesDto> {
        return coursesCollection().whereArrayContains(
            FirebaseConstants.Firestore.STUDENTS,
            studentId
        ).get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject<CoursesDto>()
        }
    }

    override suspend fun teachingCourses(teacherId: String): List<CoursesDto> {
        return coursesCollection().whereArrayContains(
            FirebaseConstants.Firestore.TEACHERS,
            teacherId
        ).get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject<CoursesDto>()
        }
    }

    override suspend fun updateCourse(courseId: String, coursesDto: CoursesDto) {
        coursesCollection().document(courseId).update(coursesDto.toMap()).await()
    }

    private fun coursesCollection(): CollectionReference =
        firestore.collection(FirebaseConstants.Firestore.COURSES_COLLECTION)
}